package com.sellics.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sellics.client.AmazonSearchVolumeClient;
import com.sellics.dto.AmazonSearchVolume;
import com.sellics.dto.SearchVolumeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class SearchVolumeServiceImpl implements SearchVolumeService {

    private static final long TIME_OUT_IN_MS = 10 * 1000;
    private AmazonSearchVolumeClient client;
    private ExecutorService executorService;

    public SearchVolumeServiceImpl(AmazonSearchVolumeClient client, ExecutorService executorService) {
        this.client = client;
        this.executorService = executorService;
    }

    /**
     * Estimate score for a given prefix
     */
    @Override
    public SearchVolumeResponse getEstimatedSearchScoreByPrefix(String prefix) {
        prefix = preProcess(prefix);
        if (StringUtils.isBlank(prefix)) {
            return new SearchVolumeResponse(prefix, 0);
        }
        List<AmazonSearchVolume> amazonSearchVolumes = getAmazonSearchesByPrefix(prefix);
        return estimateScore(amazonSearchVolumes, prefix);

    }

    /**
     * Trim given prefix
     * Remove '+' with blank
     */
    private String preProcess(String prefix) {
        prefix = StringUtils.trimToEmpty(prefix);
        prefix = prefix.replace("+", " ");
        return prefix;
    }

    private SearchVolumeResponse estimateScore(List<AmazonSearchVolume> amazonSearchVolumes, String prefix) {

        double finalScore = amazonSearchVolumes.stream()
                .filter(Objects::nonNull)
                .mapToDouble(amazonSearchVolume -> estimateScore(amazonSearchVolume, prefix))
                .sum();

        double avgScore = finalScore / amazonSearchVolumes.size();
        return new SearchVolumeResponse(prefix, (int) avgScore); //rounding off
    }

    /**
     * Estimate keyword score by formula (n-i)/n * 100
     * n= number of suggestion
     * i= position of the prefix
     */
    private double estimateScore(AmazonSearchVolume amazonSearchVolume, String prefix) {
        double score = 0;
        int numberOfSuggestions = amazonSearchVolume.getSuggestions().size();
        int matchedIndex = IntStream.range(0, numberOfSuggestions)
                .filter(i -> amazonSearchVolume.getSuggestionValueAt(i).startsWith(prefix))
                .findFirst()
                .orElse(-1);
        if (matchedIndex != -1) {
            score = ((double) (numberOfSuggestions - matchedIndex) / numberOfSuggestions) * 100.0;
        }
        return score;
    }

    /**
     * For each letter in the prefix fetch list of suggestions
     * Each call timeout is TIME_OUT_IN_MS = 10 sec
     * Call to this method is wrapped in Hystrix and fallback is
     * empty list
     *
     * @param prefix
     * @return
     */
    @HystrixCommand(fallbackMethod = "getDefaultResponse")
    private List<AmazonSearchVolume> getAmazonSearchesByPrefix(String prefix) {
        // list of calls to be made ... we call this via executor concurrently
        List<Callable<AmazonSearchVolume>> callables = IntStream.rangeClosed(1, prefix.length())
                .mapToObj(i -> prefix.substring(0, i))
                .filter(StringUtils::isNotBlank)
                .map(key -> (Callable<AmazonSearchVolume>) () -> client.getSearchVolumeByPrefix(key))
                .collect(Collectors.toList());
        try {
            List<Future<AmazonSearchVolume>> futureResults = executorService.invokeAll(callables, TIME_OUT_IN_MS,
                    TimeUnit.MILLISECONDS);
            return futureResults.stream()
                    .filter(future -> !future.isCancelled())
                    .map(this::executeFuture)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (InterruptedException | CancellationException e) {
            log.error("Future exception occured", e);
        }
        return Collections.emptyList();
    }

    private List<AmazonSearchVolume> getDefaultResponse(String prefix) {
        return Collections.emptyList();
    }

    private AmazonSearchVolume executeFuture(Future<AmazonSearchVolume> future) {
        log.debug("Executing future {}", future);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Future exception occured", e);
        }
        return null;
    }

}

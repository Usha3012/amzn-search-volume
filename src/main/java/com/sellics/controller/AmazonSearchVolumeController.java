package com.sellics.controller;

import com.sellics.dto.SearchVolumeResponse;
import com.sellics.service.SearchVolumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmazonSearchVolumeController {
    private SearchVolumeService searchVolumeService;

    public AmazonSearchVolumeController(SearchVolumeService searchVolumeService) {
        this.searchVolumeService = searchVolumeService;
    }

    @RequestMapping(path = "/estimate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchVolumeResponse> estimateSearchVolume(@RequestParam("keyword") String keyword) {
        SearchVolumeResponse searchVolumeResponse = searchVolumeService.getEstimatedSearchScoreByPrefix(keyword);
        return new ResponseEntity<>(searchVolumeResponse, HttpStatus.OK);
    }
}

package com.sellics.service;

import com.sellics.dto.SearchVolumeResponse;

public interface SearchVolumeService {
    SearchVolumeResponse getEstimatedSearchScoreByPrefix(String prefix);
}

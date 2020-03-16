package com.sellics.dto;

public class SearchVolumeResponse {
    private String keyword;
    private int score;

    public SearchVolumeResponse(String keyword, int score) {
        this.keyword = keyword;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String getKeyword() {
        return keyword;
    }

}

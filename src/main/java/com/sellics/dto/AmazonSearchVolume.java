package com.sellics.dto;

import java.util.LinkedList;
import java.util.List;

public class AmazonSearchVolume {
    List<Suggestion> suggestions = new LinkedList<>();

    public AmazonSearchVolume() {

    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public String getSuggestionValueAt(int i) {
        if (i > 0 && i < suggestions.size()) {
            return suggestions.get(i).getValue();
        }
        return "";
    }
}

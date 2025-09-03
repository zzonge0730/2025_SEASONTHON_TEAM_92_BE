package com.tenantcollective.rentnegotiation.model;

public class LetterResponse {
    private String generatedText;
    private Integer tokens;
    private Boolean usedLlm;

    public LetterResponse() {}

    public LetterResponse(String generatedText, Integer tokens, Boolean usedLlm) {
        this.generatedText = generatedText;
        this.tokens = tokens;
        this.usedLlm = usedLlm;
    }

    // Getters and Setters
    public String getGeneratedText() {
        return generatedText;
    }

    public void setGeneratedText(String generatedText) {
        this.generatedText = generatedText;
    }

    public Integer getTokens() {
        return tokens;
    }

    public void setTokens(Integer tokens) {
        this.tokens = tokens;
    }

    public Boolean getUsedLlm() {
        return usedLlm;
    }

    public void setUsedLlm(Boolean usedLlm) {
        this.usedLlm = usedLlm;
    }
}
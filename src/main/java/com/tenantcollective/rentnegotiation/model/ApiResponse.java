package com.tenantcollective.rentnegotiation.model;

public class ApiResponse<T> {
    private boolean ok;
    private T data;
    private String message;

    public ApiResponse() {}

    public ApiResponse(boolean ok, T data) {
        this.ok = ok;
        this.data = data;
    }

    public ApiResponse(boolean ok, T data, String message) {
        this.ok = ok;
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
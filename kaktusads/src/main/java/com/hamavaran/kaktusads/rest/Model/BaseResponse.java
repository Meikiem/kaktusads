package com.hamavaran.kaktusads.rest.Model;

public class BaseResponse {
    private String message;
    private int status;

    public BaseResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}

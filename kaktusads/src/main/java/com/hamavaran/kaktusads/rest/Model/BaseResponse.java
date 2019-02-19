package com.hamavaran.kaktusads.rest.Model;

public class BaseResponse {
    private int status;

    BaseResponse(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }
}

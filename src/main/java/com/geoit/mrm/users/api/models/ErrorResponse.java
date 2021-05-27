package com.geoit.mrm.users.api.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String message;
    private String code;
    @SuppressWarnings("unused")
	private String error;
    private String path;

    public ErrorResponse() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.message = "No message available";
        this.timestamp = new Date();
        this.error = null;
    }

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
        this.timestamp = new Date();
    }

    public ErrorResponse(HttpStatus status, String message, String code) {
        this.status = status.value();
        this.message = message;
        this.code = code;
        this.timestamp = new Date();
    }

    public String getError() {
        return HttpStatus.valueOf(status).getReasonPhrase();
    }
}

package com.github.blarc.exception;

import com.github.blarc.model.ErrorCodeEnum;
import jakarta.ws.rs.core.Response;

public class ExpectedCustomerServiceException
        extends RuntimeException
{
    private final int httpStatusCode;
    private final ErrorCodeEnum errorCodeEnum;

    public ExpectedCustomerServiceException(String message, Response.Status httpStatusCode, ErrorCodeEnum errorCodeEnum, Throwable throwable)
    {
        super(message, throwable);
        this.httpStatusCode = httpStatusCode.getStatusCode();
        this.errorCodeEnum = errorCodeEnum;
    }

    public ExpectedCustomerServiceException(String message, int httpStatusCode, ErrorCodeEnum errorCodeEnum, Throwable throwable)
    {
        super(message, throwable);
        this.httpStatusCode = httpStatusCode;
        this.errorCodeEnum = errorCodeEnum;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public ErrorCodeEnum getErrorCodeEnum() {
        return errorCodeEnum;
    }
}

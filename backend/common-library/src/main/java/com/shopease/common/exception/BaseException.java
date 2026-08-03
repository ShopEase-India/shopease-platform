package com.shopease.common.exception;

import com.shopease.common.enums.ErrorCode;
import lombok.Getter;

/**
 * Base exception for all ShopEase application exceptions.
 */
@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
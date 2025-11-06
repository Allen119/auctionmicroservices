package com.onepiece.bidding_service.exception;

/**
 * Exception thrown when account is locked due to security reasons
 */
public class AccountLockedException extends RuntimeException {
    
    public AccountLockedException(String message) {
        super(message);
    }
    
    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}

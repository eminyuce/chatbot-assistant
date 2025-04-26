package com.yuce.chat.assistant.exception;

public class ServiceRoleSecurityException extends SecurityException {
    public ServiceRoleSecurityException(String errorMessage) {
        super(errorMessage);
    }
}

package com.yuce.chat.assistant.exception;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus {
    private String message;
    private int statusCode;
    private String errorDescription;
    private String timestamp = java.time.LocalDateTime.now().toString();
    ;

    // Custom constructor for only three fields
    public ServiceStatus(String message, int statusCode, String errorDescription) {
        this.message = message;
        this.statusCode = statusCode;
        this.errorDescription = errorDescription;
        this.timestamp = java.time.LocalDateTime.now().toString(); // set default timestamp value
    }
}

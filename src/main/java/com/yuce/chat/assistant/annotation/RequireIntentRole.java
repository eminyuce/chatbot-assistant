package com.yuce.chat.assistant.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // <-- MUST be RUNTIME
@Target(ElementType.METHOD)         // <-- MUST include METHOD
public @interface RequireIntentRole {
    String[] value(); // list of roles required
}
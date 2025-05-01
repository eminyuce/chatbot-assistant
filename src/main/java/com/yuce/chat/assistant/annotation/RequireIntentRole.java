package com.yuce.chat.assistant.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // <-- MUST be RUNTIME
@Target(ElementType.METHOD)         // <-- MUST include METHOD
public @interface RequireIntentRole {
    String[] value(); // list of roles required
}
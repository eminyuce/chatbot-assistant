package com.yuce.chat.assistant.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireIntentRole {
    String[] value(); // list of roles required
}
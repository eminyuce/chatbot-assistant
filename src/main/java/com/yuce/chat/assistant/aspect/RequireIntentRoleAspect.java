package com.yuce.chat.assistant.aspect;

import com.yuce.chat.assistant.annotation.RequireIntentRole;
import com.yuce.chat.assistant.exception.ServiceRoleSecurityException;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class RequireIntentRoleAspect {

    @Before("@annotation(requireIntentRole)")
    public void checkIntentRole(JoinPoint joinPoint, RequireIntentRole requireIntentRole) {
        // Try to find IntentExtractionResult from method arguments
        Object[] args = joinPoint.getArgs();
        IntentExtractionResult intent = Arrays.stream(args)
                .filter(IntentExtractionResult.class::isInstance)
                .map(IntentExtractionResult.class::cast)
                .findFirst()
                .orElse(null);

        if (intent == null) {
            throw new IllegalArgumentException("Method must have IntentExtractionResult parameter for @RequireIntentRole to work.");
        }

        if (!intent.hasAccessRole(requireIntentRole.value())) {
            throw new ServiceRoleSecurityException("Access denied: missing required roles " + Arrays.toString(requireIntentRole.value()));
        }
    }
}
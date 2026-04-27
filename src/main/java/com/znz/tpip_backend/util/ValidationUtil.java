package com.znz.tpip_backend.util;

import com.znz.tpip_backend.exception.ValidationException;

public class ValidationUtil {

    public static void require(Object value, String message) {
        if (value == null || (value instanceof String && ((String) value).isBlank())) {
            throw new ValidationException(message);
        }
    }
}
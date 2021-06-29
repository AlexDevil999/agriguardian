package com.agriguardian.util;

import com.agriguardian.exception.BadRequestException;
import org.springframework.validation.Errors;

public class DtoValidationUtil {

    public static void handleErrors(Errors errors) {
        if (errors.hasErrors()) {
            throw new BadRequestException(errors.getAllErrors().get(0).getDefaultMessage());
        }
    }
}

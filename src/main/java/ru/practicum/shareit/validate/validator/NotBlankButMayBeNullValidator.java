package ru.practicum.shareit.validate.validator;

import ru.practicum.shareit.validate.annotation.NotBlankButMayBeNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBlankButMayBeNullValidator implements ConstraintValidator<NotBlankButMayBeNull, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !value.isBlank();
    }
}

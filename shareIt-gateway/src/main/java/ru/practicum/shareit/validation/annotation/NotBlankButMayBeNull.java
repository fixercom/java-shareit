package ru.practicum.shareit.validation.annotation;

import ru.practicum.shareit.validation.validator.NotBlankButMayBeNullValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankButMayBeNullValidator.class)
public @interface NotBlankButMayBeNull {
    String message() default "{NotBlankButMayBeNull.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

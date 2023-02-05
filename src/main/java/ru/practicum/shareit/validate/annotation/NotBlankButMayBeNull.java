package ru.practicum.shareit.validate.annotation;

import ru.practicum.shareit.validate.validator.NotBlankButMayBeNullValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankButMayBeNullValidator.class)
public @interface NotBlankButMayBeNull {
    String message() default "{NotBlankButMayBeNull.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

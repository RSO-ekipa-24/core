package essa.validation.propertyidvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PropertyIdValidator.class)
public @interface ValidPropertyId {
    String message() default "Property id must be a positive number and exist in the database.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package essa.validation.propertygroupidvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PropertyGroupIdValidator.class)
public @interface ValidPropertyGroupId {
    String message() default "Property group id must be a positive number and exist in the database.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

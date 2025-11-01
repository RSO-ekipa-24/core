package essa.validation.propertyidvalidator;

import essa.repository.property.PropertyRepository;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PropertyIdValidator implements ConstraintValidator<ValidPropertyId, Long> {

    @Inject
    PropertyRepository propertyRepository;

    private boolean doesPropertyExist(Long id) {
        return propertyRepository.findById(id) != null;
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value > 0) {
            return doesPropertyExist(value);
        }

        return false;
    }
}

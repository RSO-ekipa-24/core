package essa.validation.propertygroupidvalidator;

import essa.repository.propertygroup.PropertyGroupRepository;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PropertyGroupIdValidator implements ConstraintValidator<ValidPropertyGroupId, Long> {

    @Inject
    PropertyGroupRepository propertyGroupRepository;

    private boolean doesPropertyGroupExist(Long id) {
        return propertyGroupRepository.findById(id) != null;
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value > 0) {
            return doesPropertyGroupExist(value);
        }

        return false;
    }
}

package vn.com.sociallistening.manager.api.constraints;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class MatchesPasswordAndRePasswordValidator implements ConstraintValidator<MatchesPasswordAndRePassword, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchesPasswordAndRePasswordValidator.class);

    private String passwordFieldName;
    private String rePasswordFieldName;
    private String message;

    @Override
    public void initialize(MatchesPasswordAndRePassword matchesPasswordAndRePassword) {
        this.passwordFieldName = matchesPasswordAndRePassword.passwordFieldName();
        this.rePasswordFieldName = matchesPasswordAndRePassword.rePasswordFieldName();
        this.message = matchesPasswordAndRePassword.message();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        Object passwordFieldVal, rePasswordFieldVal;

        try {
            passwordFieldVal = BeanUtils.getProperty(o, passwordFieldName);
            rePasswordFieldVal = BeanUtils.getProperty(o, rePasswordFieldName);

            if (passwordFieldVal.equals(rePasswordFieldVal)) {
                return true;
            } else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(passwordFieldName)
                        .addConstraintViolation();

                return false;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //e.printStackTrace();
            LOGGER.error("isValid error.", e);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(passwordFieldName)
                    .addConstraintViolation();

            return false;
        }
    }
}

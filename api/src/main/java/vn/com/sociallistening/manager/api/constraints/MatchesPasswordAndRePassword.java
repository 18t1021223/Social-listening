package vn.com.sociallistening.manager.api.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MatchesPasswordAndRePasswordValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchesPasswordAndRePassword {
    String message() default "Retype password and Password must be matched.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String passwordFieldName();
    String rePasswordFieldName();
}

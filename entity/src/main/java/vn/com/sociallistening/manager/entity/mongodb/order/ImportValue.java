package vn.com.sociallistening.manager.entity.mongodb.order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ImportValue {
    String[] target() default {};

    String source() default "";

    /**
     * source maybe null;
     * when id null then mapping to target null
     */
    boolean sourceIsNull() default false;
}

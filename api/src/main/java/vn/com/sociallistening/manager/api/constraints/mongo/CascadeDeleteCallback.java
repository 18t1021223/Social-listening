package vn.com.sociallistening.manager.api.constraints.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import vn.com.sociallistening.manager.entity.mongodb.order.Cascade;
import vn.com.sociallistening.manager.entity.mongodb.order.CascadeType;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
public class CascadeDeleteCallback implements ReflectionUtils.FieldCallback {
    private final Object source;
    private final MongoOperations mongoOperations;

    @Transactional(rollbackFor = {Exception.class}, isolation = Isolation.SERIALIZABLE)
    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);

        if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(Cascade.class)) {
            final Object fieldValue = field.get(source);

            if (Objects.nonNull(fieldValue)) {
                final CascadeType cascadeType = field.getAnnotation(Cascade.class).value();

                if (cascadeType.equals(CascadeType.DELETE) || cascadeType.equals(CascadeType.ALL)) {
                    if (fieldValue instanceof Collection<?>) {
                        ((Collection<?>) fieldValue).forEach(mongoOperations::remove);
                    } else {
                        mongoOperations.remove(fieldValue);
                    }
                }
            }
        }
    }
}

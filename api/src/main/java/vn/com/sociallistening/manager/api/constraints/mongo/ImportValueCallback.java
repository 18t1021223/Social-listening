package vn.com.sociallistening.manager.api.constraints.mongo;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import vn.com.sociallistening.manager.entity.mongodb.order.ImportValue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class ImportValueCallback implements ReflectionUtils.FieldCallback {
    private final Object source;

    private Object valueImport;

    @Override
    public void doWith(Field field) throws IllegalArgumentException {
        field.setAccessible(true);
        if (field.isAnnotationPresent(ImportValue.class)) {
            final String sourceFieldName = field.getAnnotation(ImportValue.class).source();
            String[] targetFieldName = field.getAnnotation(ImportValue.class).target();

            Object fieldValue;
            try {
                // id
                Field sourceField = source.getClass().getDeclaredField(sourceFieldName);
                sourceField.setAccessible(true);
                valueImport = sourceField.get(source);
                if (Objects.isNull(valueImport) && !field.getAnnotation(ImportValue.class).sourceIsNull())
                    throw new NullPointerException("source cannot possibly be null");
                //id
                fieldValue = field.get(source);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }

            if (Objects.nonNull(fieldValue))
                if (fieldValue instanceof Collection<?>) {
                    ((Collection<?>) fieldValue).forEach(valueChild -> {
                        if (Objects.nonNull(valueChild))
                            setValue(targetFieldName, valueChild);
                    });
                } else
                    setValue(targetFieldName, fieldValue);

        }
    }

    private void setValue(String[] targetFieldName, Object fieldValue) {
        Arrays.stream(targetFieldName).forEach(value -> {
            try {
                Field temp = fieldValue.getClass().getDeclaredField(value);
                temp.setAccessible(true);
                temp.set(fieldValue, valueImport);
            } catch (NoSuchFieldException e) {
                log.warn("{}", e);
                throw new RuntimeException("Field " + value + " not found");
            } catch (IllegalAccessException e) {
                log.warn("{}", e);
                throw new RuntimeException("Field " + value + " cannot access");
            }
        });
    }
}
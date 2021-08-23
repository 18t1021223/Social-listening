package vn.com.sociallistening.manager.api;

import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import vn.com.sociallistening.manager.api.pojos.MetaData;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class Utils {
    public static final Map<String, Object> buildResponse(int error, String message, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", error);
        map.put("message", message);
        map.put("data", data);
        return map;
    }

    public static final Map<String, Object> buildResponse(int error, String message, Object data, List<FieldError> fieldErrors) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", error);
        map.put("message", message);
        map.put("data", data);

        List<Map<String, Object>> maps = new ArrayList<>();

        fieldErrors.forEach(fieldError -> {
            Map<String, Object> field = new HashMap<>();
            field.put("name", fieldError.getField());
            field.put("message", fieldError.getDefaultMessage());
            maps.add(field);
        });

        map.put("fieldErrors", maps);
        return map;
    }

    public static MetaData getMetaData(HttpServletRequest request) throws Exception {
        MetaData metaData = new MetaData();
        metaData.setPage(StringUtils.isEmpty(request.getParameter("page")) ? 1 : Integer.parseInt(request.getParameter("page")));
        metaData.setSize(StringUtils.isEmpty(request.getParameter("size")) ? 20 : Integer.parseInt(request.getParameter("size")));
        metaData.setDirection(StringUtils.isEmpty(request.getParameter("sort")) ? null : request.getParameter("sort").equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC);
        metaData.setField(StringUtils.isEmpty(request.getParameter("field")) ? org.apache.commons.lang3.StringUtils.EMPTY : request.getParameter("field"));
        if (metaData.getSize() > 100) metaData.setSize(100);
        return metaData;
    }

    public static Map<String, Object> convertToMapIncludes(Object object, String[] includes) throws Exception {
        Map<String, Object> item = new HashMap<>();
        List<String> strings = Arrays.asList(includes);
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAccessible()) field.setAccessible(true);
            if (strings.contains(field.getName())) {
                item.put(field.getName(), field.get(object));
            }
        }

        return item;
    }

    public static Map<String, Object> convertToMapExcludes(Object object, String[] excludes) throws Exception {
        Map<String, Object> item = new HashMap<>();
        List<String> strings = Arrays.asList(excludes);
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAccessible()) field.setAccessible(true);
            if (!strings.contains(field.getName())) {
                item.put(field.getName(), field.get(object));
            }
        }

        return item;
    }

    public static BigDecimal toPrecision(BigDecimal dec, int precision) {
        String plain = dec.movePointRight(precision).toPlainString();
        return new BigDecimal(plain.substring(0, plain.indexOf("."))).movePointLeft(precision);
    }
}

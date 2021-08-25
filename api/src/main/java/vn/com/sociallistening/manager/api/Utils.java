package vn.com.sociallistening.manager.api;

import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import vn.com.sociallistening.manager.api.pojos.MetaData;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
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

    public static Object getValue(Object data, int index) {
        if (data == null) return null;
        if (data instanceof List)
            return ((List<?>) data).get(index);
        return null;
    }

    /**
     * facebook maybe error
     *
     * @param dateInString
     * @return
     */
    public static String handlePostDate(String dateInString) {
        DateTimeFormatter formatter;
        LocalDateTime dateTime;
        LocalDate date;
        //about 3 weeks ago
        //about an hour ago
        //about a month ago
        if (dateInString.startsWith("about ")) {
            dateInString = dateInString.substring(dateInString.indexOf(' ') + 1);
            if (dateInString.startsWith("a ") || dateInString.startsWith("an "))
                dateInString = "1 " + dateInString.substring(dateInString.indexOf(' ') + 1);
        }

        if (dateInString.contains(",")) {
            if (dateInString.contains(" at ")) {
                // October 22, 2019 at 4:20 PM
                dateInString = dateInString.replace(" at ", " ");
                formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a", Locale.ENGLISH);
                dateTime = LocalDateTime.parse(dateInString, formatter);
                dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));
            } else {
                //March 18, 2012
                formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
                date = LocalDate.parse(dateInString, formatter);
                dateInString = new SimpleDateFormat("yyyy-MM-dd 00:00").format(java.sql.Date.valueOf(date));
            }

        } else if (dateInString.contains(" at ")) {
            //Yesterday at 1:00 PM
            if (dateInString.contains("Yesterday")) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a", Locale.ENGLISH);
                date = LocalDate.now().minusDays(1);
                dateTime = LocalDateTime.parse(date + dateInString.substring(dateInString.indexOf("at") + 2),
                        formatter);
                dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));

            } else if (Character.isDigit(dateInString.charAt(0))) {
                if (!Character.isDigit(dateInString.charAt(dateInString.indexOf(" at ") - 2))) {
                    // 20 June at 10:55
                    formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy H:mm", Locale.ENGLISH);
                    dateInString = dateInString.replace("at", Year.now().toString());
                    dateTime = LocalDateTime.parse(dateInString, formatter);
                    dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));
                } else {
                    // 20 June 2020 at 10:55
                    formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH);
                    dateInString = dateInString.replace(" at ", " ");
                    dateTime = LocalDateTime.parse(dateInString, formatter);
                    dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));
                }
            } else {
                //May 13 at 3:23 PM
                formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a", Locale.ENGLISH);
                dateInString = dateInString.replace(" at", ", " + Year.now());
                dateTime = LocalDateTime.parse(dateInString, formatter);
                dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));
            }
        } else if (!Character.isDigit(dateInString.charAt(0))) {
            if (dateInString.toLowerCase().contains("now") || dateInString.toLowerCase().contains("sec")) {
                dateTime = LocalDateTime.now();
                dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));
            } else if (dateInString.startsWith("on ")) {
                //on Fri    -> fri on wk ago
                date = LocalDate.now();
                dateInString = dateInString.substring(dateInString.indexOf(' ') + 1);
                while (!date.getDayOfWeek().toString().startsWith(dateInString.toUpperCase())) {
                    date = date.minusDays(1);
                }
                dateInString = new SimpleDateFormat("yyyy-MM-dd 00:00").format(java.sql.Date.valueOf(date));
            } else if (dateInString.startsWith("last")) {
                //last Mon    last mon of month
                date = LocalDate.now().minusWeeks(1);
                dateInString = dateInString.substring(dateInString.indexOf(' ') + 1);
                while (!date.getDayOfWeek().toString().contains(dateInString.toUpperCase()))
                    date = date.plusDays(1);
                dateInString = new SimpleDateFormat("yyyy-MM-dd 00:00").format(java.sql.Date.valueOf(date));
            } else {
                //March 18
                formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
                dateInString += ", " + Year.now();
                date = LocalDate.parse(dateInString, formatter);
                dateInString = new SimpleDateFormat("yyyy-MM-dd 00:00").format(java.sql.Date.valueOf(date));
            }
        } else if (Character.isDigit(dateInString.charAt(0))) {

            if (dateInString.endsWith("m") || dateInString.contains("min")) {
                //3m
                dateTime = LocalDateTime.now();
                dateTime = dateTime.minusMinutes(Integer.parseInt(dateInString.substring(0, dateInString.indexOf("m")).trim()));
                dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));
            } else if (dateInString.endsWith("h") || dateInString.contains("hr") || dateInString.contains("hour")) {
                //2h
                dateTime = LocalDateTime.now();
                dateTime = dateTime.minusHours(Integer.parseInt(dateInString.substring(0, dateInString.indexOf("h")).trim()));
                dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Timestamp.valueOf(dateTime));
            } else if (dateInString.endsWith("d")) {
                //4d
                date = LocalDate.now();
                date = date.minusDays(Integer.parseInt(dateInString.substring(0, dateInString.indexOf("d")).trim()));
                dateInString = new SimpleDateFormat("yyyy-MM-dd HH:00").format(java.sql.Date.valueOf(date));
            } else if (dateInString.endsWith("w") || dateInString.endsWith("wk") || dateInString.endsWith("wks") || dateInString.contains("week")) {
                //5w
                date = LocalDate.now();
                date = date.minusWeeks(Integer.parseInt(dateInString.substring(0, dateInString.indexOf("w")).trim()));
                dateInString = new SimpleDateFormat("yyyy-MM-dd 00:00").format(java.sql.Date.valueOf(date));
            } else if (dateInString.contains("mo")) {
                //2mon
                date = LocalDate.now();
                date = date.minusMonths(Integer.parseInt(dateInString.substring(0, dateInString.indexOf("mo")).trim()));
                dateInString = new SimpleDateFormat("yyyy-MM-dd 00:00").format(java.sql.Date.valueOf(date));
            } else if (dateInString.endsWith("y") || dateInString.contains("year")) {
                //5y
                date = LocalDate.now();
                date = date.minusYears(Integer.parseInt(dateInString.substring(0, dateInString.indexOf("y")).trim()));
                dateInString = new SimpleDateFormat("yyyy-MM-dd 00:00").format(java.sql.Date.valueOf(date));
            }
        } else {
            throw new UnsupportedOperationException("not handle postDate: " + dateInString);
        }
        return dateInString;
    }
}

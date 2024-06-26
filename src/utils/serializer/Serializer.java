package utils.serializer;

import utils.logger.Logger;
import utils.logger.LoggerLevel;

import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * This class contains methods to serialize/deserialize objects in/from strings
 */
@SuppressWarnings("all")
public class Serializer {

    private static final Logger LOGGER = new Logger("Serializer");

    /**
     * This method serializes each one of the object's field and value into a string.
     * <p>
     * Example:
     * fieldName:fieldValue
     *
     * @param object The object we need to serialize.
     * @param fields The object's fields.
     * @return Serialized object in a String value.
     */
    public static <T> String serializeObject(T object, Field[] fields) {
        StringBuilder values = new StringBuilder();
        int count = 0;

        try {
            for(Field field : fields) {
                field.setAccessible(true);
                if (count == fields.length - 1) {
                    values.append(field.getName()).append(":").append(field.get(object));
                }
                else if (fields.length == 1) {
                    values.append(field.getName()).append(":").append(field.get(object));
                }
                else {
                    values.append(field.getName()).append(":").append(field.get(object)).append(";");
                }

                count++;
            }
        }
        catch (IllegalAccessException e) {
            LOGGER.log(LoggerLevel.ERROR, "Serializing error. A field is not accessible.");
        }

        return values.toString();
    }


    /**
     * This method deserializes a String value into an Object.
     *
     * @param object The object we need to deserialize.
     * @param value The object in string form.
     */
    public static <T> void deserializeObject(T object, String value) throws NoSuchFieldException, IllegalAccessException {
        LinkedList<String> values = getValues(value, ';');

        for(String s : values) {
            LinkedList<String> temp = getValues(s, ':');

            if(temp.size() == 1){
                setField(object, (String)temp.get(0), "");
            }
            else{
                setField(object, (String)temp.get(0), (String)temp.get(1));
            }
        }
    }

    private static <T> void setField(T object, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, fromStringValue(field, value));
    }

    private static Object fromStringValue(Field field, String value) {
        return toObject(field.getType(), value);
    }

    private static Object toObject(Class clazz, String value) {
        if(Boolean.class == clazz || boolean.class == clazz) return Boolean.parseBoolean( value );
        if(Byte.class == clazz || byte.class == clazz) return Byte.parseByte( value );
        if(Short.class == clazz || short.class == clazz) return Short.parseShort( value );
        if(Integer.class == clazz || int.class == clazz) return Integer.parseInt( value );
        if(Long.class == clazz || long.class == clazz) return Long.parseLong( value );
        if(Float.class == clazz || float.class == clazz) return Float.parseFloat( value );
        if(Double.class == clazz || double.class == clazz) return Double.parseDouble( value );
        return value;
    }

    private static LinkedList<String> getValues(String value, char divider) {
        LinkedList<String> values = new LinkedList<String>();
        for (String s : value.split(divider+"")){
            values.add(s);
        }
        return values;
    }
}

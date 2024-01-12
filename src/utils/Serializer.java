package utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Serializer {

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
        catch (IllegalAccessException var8) {
            System.out.println("ERRORE");
        }

        return values.toString();
    }

    //Apparently has huge bug
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

    private static int countDividers(String value, char divider) {
        int count = 0;

        for(char t : value.toCharArray()) {
            if (t == divider) {
                count++;
            }
        }

        return count;
    }

    private static LinkedList<Integer> getDividersIndexes(String value, char divider) {
        int index = 0;
        LinkedList<Integer> indexes = new LinkedList<Integer>();

        for(char t : value.toCharArray()) {
            if (t == divider) {
                indexes.add(index);
            }
            index++;
        }

        return indexes;
    }
}

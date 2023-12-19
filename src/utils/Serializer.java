package utils;

import com.sun.jdi.PrimitiveValue;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.LinkedList;

public class Serializer {

    public static <T> String serializeObject(T object, Field[] fields)  {
        StringBuilder values = new StringBuilder();

        int count = 0;

        try{
            for(Field field : fields){
                if(count == fields.length-1){
                    values.append(field.getName()).append(":").append(field.get(object));
                }
                else{
                    values.append(field.getName()).append(":").append(field.get(object)).append(";");
                }
                count++;
            }
        }
        catch(IllegalAccessException e){
            System.out.println("ERRORE");
        }

        return values.toString();
    }

    public static <T> void deserializeObject(T object, String value) throws NoSuchFieldException, IllegalAccessException {
        LinkedList<String> values = getValues(value, ';');

        for(String s : values){
            LinkedList<String> temp = getValues(s, ':');
            System.out.println(temp.get(1));
            setField(object, temp.get(0), temp.get(1));
        }
    }


    private static <T> void setField(T object, String fieldName, String value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getField(fieldName);
        field.set(object, fromStringValue(field, value));
    }

    private static Object fromStringValue(Field field, String value){
        return toObject(field.getType(), value);
    }

    private static Object toObject(Class clazz, String value ) {
        if(Boolean.class == clazz || boolean.class == clazz) return Boolean.parseBoolean( value );
        if(Byte.class == clazz || byte.class == clazz) return Byte.parseByte( value );
        if(Short.class == clazz || short.class == clazz) return Short.parseShort( value );
        if(Integer.class == clazz || int.class == clazz) return Integer.parseInt( value );
        if(Long.class == clazz || long.class == clazz) return Long.parseLong( value );
        if(Float.class == clazz || float.class == clazz) return Float.parseFloat( value );
        if(Double.class == clazz || double.class == clazz) return Double.parseDouble( value );
        if(clazz.isEnum()) return Enum.valueOf(clazz, value);
        return value;
    }

    private static LinkedList<String> getValues(String value, char divider){
        int dividers = countDividers(value, divider);

        LinkedList<Integer> indexes = getDividersIndexes(value, divider);

        LinkedList<String> values = new LinkedList<String>();

        for(int i = 0; i<dividers; i++){
            if(i==0 && dividers==1){
                values.add(value.substring(0, indexes.get(i)));
                values.add(value.substring(indexes.get(i)+1));
            }
            else if(i==0){
                values.add(value.substring(0, indexes.get(i)));
            }
            else if(i == dividers-1){
                values.add(value.substring(indexes.get(i)-1));
            }
            else{
                values.add(value.substring(indexes.get(i)-1, indexes.get(i+1)));
            }
        }

        return values;
    }

    private static int countDividers(String value, char divider){
        int count = 0;

        for(char t : value.toCharArray()){
            if(t == divider){
                count++;
            }
        }
        return count;
    }

    private static LinkedList<Integer> getDividersIndexes(String value, char divider){
        int index = 0;
        LinkedList<Integer> indexes = new LinkedList<Integer>();

        for(char t : value.toCharArray()){
            if(t == divider){
                indexes.add(index);
            }
            index++;
        }

        return indexes;
    }

}

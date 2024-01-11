package utils;

import java.lang.reflect.Field;
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

                ++count;
            }
        }
        catch (IllegalAccessException var8) {
            System.out.println("ERRORE");
        }

        return values.toString();
    }

    public static <T> void deserializeObject(T object, String value) throws NoSuchFieldException, IllegalAccessException {
        LinkedList<String> values = getValues(value, ';');

        for(String s : values) {
            LinkedList<String> temp = getValues(s, ':');
            setField(object, (String)temp.get(0), (String)temp.get(1));
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
        if (Boolean.class != clazz && Boolean.TYPE != clazz) {
            if (Byte.class != clazz && Byte.TYPE != clazz) {
                if (Short.class != clazz && Short.TYPE != clazz) {
                    if (Integer.class != clazz && Integer.TYPE != clazz) {
                        if (Long.class != clazz && Long.TYPE != clazz) {
                            if (Float.class != clazz && Float.TYPE != clazz) {
                                if (Double.class != clazz && Double.TYPE != clazz) {
                                    return clazz.isEnum() ? Enum.valueOf(clazz, value) : value;
                                } else {
                                    return Double.parseDouble(value);
                                }
                            } else {
                                return Float.parseFloat(value);
                            }
                        } else {
                            return Long.parseLong(value);
                        }
                    } else {
                        return Integer.parseInt(value);
                    }
                } else {
                    return Short.parseShort(value);
                }
            } else {
                return Byte.parseByte(value);
            }
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    private static LinkedList<String> getValues(String value, char divider) {
        int dividers = countDividers(value, divider);
        LinkedList<Integer> indexes = getDividersIndexes(value, divider);
        LinkedList<String> values = new LinkedList<String>();

        for(int i = 0; i < dividers; ++i) {
            if (i == 0 && dividers == 1) {
                values.add(value.substring(0, (Integer)indexes.get(i)));
                values.add(value.substring((Integer)indexes.get(i) + 1));
            }
            else if (i == 0) {
                values.add(value.substring(0, (Integer)indexes.get(i)));
            }
            else if (i == dividers - 1) {
                values.add(value.substring((Integer)indexes.get(i) + 1));
            }
            else {
                values.add(value.substring((Integer)indexes.get(i) + 1, (Integer)indexes.get(i + 1)));
            }
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
        LinkedList<Integer> indexes = new LinkedList();

        for(char t : value.toCharArray()) {
            if (t == divider) {
                indexes.add(index);
            }

            index++;
        }

        return indexes;
    }
}

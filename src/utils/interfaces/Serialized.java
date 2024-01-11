package utils.interfaces;


public interface Serialized {
    void read(String value) throws NoSuchFieldException, IllegalAccessException;
}

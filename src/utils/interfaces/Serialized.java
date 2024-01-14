package utils.interfaces;

/**
 * This is an interface for classes that need to be Serialized in a string.
 */
public interface Serialized {
    void read(String value) throws NoSuchFieldException, IllegalAccessException;
}

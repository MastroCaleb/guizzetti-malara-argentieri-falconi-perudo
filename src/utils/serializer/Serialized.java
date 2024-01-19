package utils.serializer;

/**
 * This is an interface for classes that need to be Serialized in a string.
 */
public class Serialized {
    public void read(String value) throws NoSuchFieldException, IllegalAccessException {
        Serializer.deserializeObject(this, value);
    }
}

package utils.interfaces;

public interface Serialized {
    public String toString();

    public void fromString(String value) throws NoSuchFieldException, IllegalAccessException;
}

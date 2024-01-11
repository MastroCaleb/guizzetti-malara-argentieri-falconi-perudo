package network.packets;

import utils.Serializer;
import utils.interfaces.Serialized;

public class Packet implements Serialized {
    public String write() {
        return Serializer.serializeObject(this, this.getClass().getDeclaredFields());
    }

    public void read(String value) throws NoSuchFieldException, IllegalAccessException {
        Serializer.deserializeObject(this, value);
    }
}

package network.packets;

import utils.serializer.Serializer;
import utils.interfaces.Serialized;

/**
 * Base for each Packet.
 */
public class Packet implements Serialized {
    public String write() {
        return Serializer.serializeObject(this, this.getClass().getDeclaredFields());
    }

    public void read(String value) throws NoSuchFieldException, IllegalAccessException {
        Serializer.deserializeObject(this, value);
    }
}

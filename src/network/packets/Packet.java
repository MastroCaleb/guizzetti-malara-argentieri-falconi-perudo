package network.packets;

import utils.serializer.Serializer;
import utils.serializer.Serialized;

/**
 * Base for each Packet.
 */
public class Packet extends Serialized {
    public String write() {
        return Serializer.serializeObject(this, this.getClass().getDeclaredFields());
    }
}

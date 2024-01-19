package network.packets;

import utils.serializer.Serializer;
import utils.serializer.Serialized;

/**
 * Base for each Packet.
 */
public class Packet extends Serialized {
    /**
     * @return Serializes this object as a String.
     */
    public String write() {
        return Serializer.serializeObject(this, this.getClass().getDeclaredFields());
    }
}

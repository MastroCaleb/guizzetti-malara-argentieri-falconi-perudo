package network.packets.action;

import network.packets.Packet;

/**
 * Packet that determines a numerical action.
 */
public class ActionPacket extends Packet {

    /**
     * The choice sent in from the packet.
     */
    private final String choice;

    /**
     * The constructor of the class.
     * @param choice The choice sent in from the packet.
     */
    public ActionPacket(String choice) {
        this.choice = choice;
    }

    /**
     * @return The choice made.
     */
    public String getChoice() {
        return "action:" + this.choice;
    }
}

package network.packets.action;

import network.packets.Packet;

/**
 * Packet that determines a numerical action.
 */
public class ActionPacket extends Packet {
    private final String choice;

    public ActionPacket(String choice) {
        this.choice = choice;
    }

    public String getChoice() {
        return "action:" + this.choice;
    }
}

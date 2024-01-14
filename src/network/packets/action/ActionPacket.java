package network.packets.action;

import network.packets.Packet;

@Deprecated
public class ActionPacket extends Packet {
    private final String choice;

    public ActionPacket(String choice) {
        this.choice = choice;
    }

    public String getChoice() {
        return this.choice;
    }
}

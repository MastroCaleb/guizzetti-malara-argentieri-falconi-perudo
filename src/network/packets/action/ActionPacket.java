package network.packets.action;

import network.packets.Packet;

public class ActionPacket extends Packet {
    private String choice;

    public ActionPacket(String choice) {
        this.choice = choice;
    }

    public String getChoice() {
        return this.choice;
    }
}

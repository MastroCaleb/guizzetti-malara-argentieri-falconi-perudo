package network.packets.bet;

import network.packets.Packet;

public class BetPacket extends Packet {
    private final int diceValue;
    private final int diceNumber;

    public BetPacket(int diceValue, int diceNumber) {
        this.diceValue = diceValue;
        this.diceNumber = diceNumber;
    }

    public int getDiceValue() {
        return this.diceValue;
    }
    public int getDiceNumber() {
        return this.diceNumber;
    }
}


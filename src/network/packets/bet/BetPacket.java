package network.packets.bet;

import network.packets.Packet;

/**
 * Packet that contains a bet.
 */
public class BetPacket extends Packet {
    private int diceValue;
    private int diceNumber;

    public BetPacket(String packet) throws NoSuchFieldException, IllegalAccessException {
        read(packet);
    }
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


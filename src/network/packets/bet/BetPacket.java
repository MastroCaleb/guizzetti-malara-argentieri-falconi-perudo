package network.packets.bet;

import network.packets.Packet;

/**
 * Packet that contains a bet.
 */
public class BetPacket extends Packet {

    /**
     * The packet's dice value.
     */
    private int diceValue;

    /**
     * The packet's dice number.
     */
    private int diceNumber;

    /**
     * The constructor of the class. It also reads a packet and deserializes it.
     * @param packet The packet in string value.
     */
    public BetPacket(String packet) throws NoSuchFieldException, IllegalAccessException {
        read(packet);
    }

    /**
     * The constructor of the class.
     * @param diceValue The packet's dice value.
     * @param diceNumber The packet's dice number.
     */
    public BetPacket(int diceValue, int diceNumber) {
        this.diceValue = diceValue;
        this.diceNumber = diceNumber;
    }

    /**
     * @return The packet's dice value.
     */
    public int getDiceValue() {
        return this.diceValue;
    }

    /**
     * @return The packet's dice number.
     */
    public int getDiceNumber() {
        return this.diceNumber;
    }
}


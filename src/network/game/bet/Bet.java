package network.game.bet;

import network.game.player.Player;
import network.packets.bet.BetPacket;

public class Bet {
    private final Player player;
    private int diceValue = 0;
    private int diceNumber = 0;

    public Bet(Player player, int diceValue, int diceNumber) {
        this.player = player;
        this.diceValue = diceValue;
        this.diceNumber = diceNumber;
    }

    public Bet(Player player, BetPacket betPacket) {
        this.player = player;
        this.diceValue = betPacket.getDiceValue();
        this.diceNumber = betPacket.getDiceNumber();
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getDiceValue() {
        return this.diceValue;
    }

    public String diceValueString() {
        return this.diceValue == 1 ? "J" : "" + this.diceValue;
    }

    public int getDiceNumber() {
        return this.diceNumber;
    }

    public String toString() {
        return "(Dice Value: " + this.diceValueString() + " | Betted Number: " + this.diceNumber + ")";
    }
}

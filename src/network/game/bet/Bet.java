package network.game.bet;

import network.game.player.Player;
import network.packets.bet.BetPacket;

/**
 * The Bet class contains information about the Bet.
 */
public class Bet {
    private final Player player; //The player that made the bet

    //The bet values
    private int diceValue = 0; //Dice Value == 1 means this is a Jolly.
    private int diceNumber = 0;

    public Bet(Player player, int diceValue, int diceNumber) {
        this.player = player;
        this.diceValue = diceValue;
        this.diceNumber = diceNumber;
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

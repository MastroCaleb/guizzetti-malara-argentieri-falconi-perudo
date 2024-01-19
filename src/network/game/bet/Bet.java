package network.game.bet;

import network.game.player.Player;

/**
 * The Bet class contains information about the Bet.
 *
 * @param player    The player that made the bet
 * @param diceValue The bet's dice value. Dice value == 1 means this is a Jolly.
 * @param diceNumber The bet's dice number.
 */
public record Bet(Player player, int diceValue, int diceNumber) {

    public String diceValueString() {
        return this.diceValue == 1 ? "J" : "" + this.diceValue;
    }

    public String toString() {
        return "(Dice Value: " + this.diceValueString() + " | Bet Number: " + this.diceNumber + ")";
    }
}

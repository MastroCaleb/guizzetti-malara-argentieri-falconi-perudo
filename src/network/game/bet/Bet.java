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

    /**
     * @return The value of the dice but as a String.
     */
    public String diceValueString() {
        return this.diceValue == 1 ? "J" : "" + this.diceValue;
    }

    /**
     * @return The Bet but in a convenient String format.
     */
    public String showBet() {
        return "(Dice Value: " + this.diceValueString() + " | Bet Number: " + this.diceNumber + ")";
    }
}

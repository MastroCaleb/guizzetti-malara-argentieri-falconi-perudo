package network.game.dice;

import java.util.Random;

/**
 * This class manages the instance of a Die.
 */
public class Dice {
    /**
     * The dice's value.
     */
    private int value;
    /**
     * If this Dice can roll to a Jolly. (This depends on the player's lobby settings).
     */
    private final boolean jollies;

    /**
     * The constructor of this class. Also rolls the dice once.
     * @param jollies Weather the dice can roll to a Jolly or not.
     */
    public Dice(boolean jollies) {
        this.jollies = jollies;
        this.roll();
    }

    /**
     * @return The dice's value as a number.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * @return The dice's value as a String.
     */
    public String toString() {
        return this.value == 1 ? "J" : "" + this.value;
    }

    /**
     * Rolls this dice to a new value.
     */
    public void roll() {
        Random random = new Random();
        int min = this.jollies ? 1 : 2;
        this.value = random.nextInt(min, 7);
    }
}

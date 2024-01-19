package network.game.dice;

import java.util.Random;

/**
 * This class manages the instance of a Die.
 */
public class Dice {
    private int value; //The dice value.
    private final boolean jollies; //If this dice can be a Jolly (we get this value from the lobby's settings)

    public Dice(boolean jollies) {
        this.jollies = jollies;
        this.roll();
    }

    public int getValue() {
        return this.value;
    }

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

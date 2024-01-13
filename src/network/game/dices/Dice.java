package network.game.dices;

import java.util.Random;

public class Dice {
    private int value;
    private boolean jollies = false;

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

    public void roll() {
        Random random = new Random();
        int min = this.jollies ? 1 : 2;
        this.value = random.nextInt(min, 7);
    }
}

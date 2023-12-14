package game.dices;

import java.util.Random;

public enum DiceValue {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX;

    public static DiceValue randomDiceValue(){
        return values()[new Random().nextInt(0, 6)];
    }
}

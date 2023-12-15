package game.bet;

import game.dices.Dice;
import game.dices.DiceValue;

import java.io.Serializable;

public class BetPacket implements Serializable {

    private int times;
    private DiceValue diceValue;

    public BetPacket(int times, DiceValue diceValue){
        this.times = times;
        this.diceValue = diceValue;
    }
    public BetPacket(){
    }

    public String toString(){
        return times + ";" + diceValue;
    }

    public void fromString(String value){
        int index = value.indexOf(";");
        String times = value.substring(0, index);
        String dice = value.substring(index+1);

        this.times = Integer.parseInt(times);
        this.diceValue = DiceValue.valueOf(dice);
    }

}

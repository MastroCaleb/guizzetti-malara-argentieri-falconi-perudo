package game.bet;

import game.dices.Dice;
import game.dices.DiceValue;
import utils.Serializer;

import java.io.Serializable;

public class BetPacket implements Serializable {
    public int times;
    public DiceValue diceValue;

    public BetPacket(int times, DiceValue diceValue){
        this.times = times;
        this.diceValue = diceValue;
    }
    public BetPacket(){
    }

    public String toString(){
        return Serializer.serializeObject(this, this.getClass().getDeclaredFields());
    }

    public void fromString(String value){
        Serializer.deserializeObject(this, value);
    }

}

package game.bet;

import game.dices.DiceValue;
import utils.Serializer;
import utils.interfaces.Serialized;

public class Bet implements Serialized {
    public int times;
    public DiceValue diceValue;

    public Bet(int times, DiceValue diceValue){
        this.times = times;
        this.diceValue = diceValue;
    }
    public Bet(){
    }

    @Override
    public String toString(){
        return Serializer.serializeObject(this, this.getClass().getDeclaredFields());
    }

    @Override
    public void fromString(String value) throws NoSuchFieldException, IllegalAccessException {
        Serializer.deserializeObject(this, value);
    }

}

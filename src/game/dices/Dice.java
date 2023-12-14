package game.dices;

public class Dice {

    private DiceValue diceValue;

    public Dice(DiceValue diceValue){
        this.diceValue = diceValue;
    }
    public Dice(){
        rollDice();
    }

    public void rollDice(){
        this.diceValue = DiceValue.randomDiceValue();
    }

}

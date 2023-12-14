package game.players;

import game.bet.BetPacket;
import game.dices.Dice;
import game.dices.DiceValue;

import java.util.LinkedList;
import java.util.Scanner;

public class Player {

    private LinkedList<Dice> dices = new LinkedList<Dice>();

    public Player(){
        for(int i=0; i<5; i++){
            dices.add(new Dice());
        }
    }

    public void removeDice(){
        dices.remove();
    }

    public BetPacket bet(){
        Scanner scan = new Scanner(System.in);

        System.out.println("Times: ");
        int times = scan.nextInt();

        System.out.println("Of value: ");
        int value = scan.nextInt();

        DiceValue diceValue = DiceValue.values()[value-1];

        return new BetPacket(times, diceValue);
    }

}

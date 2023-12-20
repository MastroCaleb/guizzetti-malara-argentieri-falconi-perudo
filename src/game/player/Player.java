package game.player;

import game.bet.Bet;
import game.dices.Dice;
import game.dices.DiceValue;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Player {
    private String name;
    private Socket client;
    private LinkedList<Dice> dices = new LinkedList<Dice>();

    public Player(String name, Socket client){
        this.name = name;
        this.client = client;

        for(int i=0; i<5; i++){
            dices.add(new Dice());
        }
    }

    public String getName() {
        return name;
    }
    public Socket getClient() {
        return client;
    }

    public void rollAll(){
        for(Dice d : dices){
            d.rollDice();
        }
    }
    public void removeDice(){
        dices.remove();
    }

    public Bet bet(){
        Scanner scan = new Scanner(System.in);

        System.out.println("Times: ");
        int times = scan.nextInt();

        System.out.println("Of value: ");
        int value = scan.nextInt();

        DiceValue diceValue = DiceValue.values()[value-1];

        return new Bet(times, diceValue);
    }
}

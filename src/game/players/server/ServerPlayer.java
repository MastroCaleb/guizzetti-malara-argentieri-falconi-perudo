package game.players.server;

import game.bet.Bet;
import game.dices.Dice;
import game.dices.DiceValue;
import game.players.client.ClientPlayer;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerPlayer {
    private String name;
    private Socket client;
    private LinkedList<Dice> dices = new LinkedList<Dice>();

    public ServerPlayer(ClientPlayer clientPlayer){
        this.name = clientPlayer.getName();
        this.client = clientPlayer.getClient();

        for(int i=0; i<5; i++){
            dices.add(new Dice());
        }
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

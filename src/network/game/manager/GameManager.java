package network.game.manager;

import network.game.bet.Bet;
import network.game.dices.Dice;
import network.game.player.Player;
import network.server.lobbies.Lobby;
import network.server.service.SockItHandler;

import java.io.IOException;
import java.util.LinkedList;

public class GameManager implements Runnable {

    private LinkedList<Player> allPlayers = new LinkedList<Player>();
    private Bet currentBet = null;
    private int round = 1;
    private Lobby lobby;
    private int playersAlive = 0;
    private boolean hasFinished = false;
    //PALIFIC
    private boolean palific = false;
    private int palificRound = 0;
    //SOCK IT
    private Player sockItUser = null;
    private boolean sockIt = false;

    public GameManager(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void run() {
        allPlayers = this.lobby.getPlayers();
        this.playersAlive = this.lobby.getPlayers().size();
        this.lobby.sendToAll("");
        this.lobby.sendToAll("Round " + this.round);
        this.lobby.sendToAll("");

        while(true) {
            for(int i = 0; i<lobby.getPlayers().size(); i++) {
                Player player = lobby.getPlayers().get(i);
                try {
                    if (player.hasDices()) {
                        if (this.playersAlive == 1 || this.lobby.getPlayers().size() == 1) {
                            this.lobby.sendToAll("[--GAME HAS ENDED--]");
                            this.lobby.sendToAll("");
                            this.lobby.sendToAll(player.getName() + " won the network.game!");
                            this.lobby.sendToAll("");

                            for(Player p : allPlayers){
                                this.lobby.sendToAll("[--LOSERS--]");
                                this.lobby.sendToAll("");
                                if(!p.hasDices()){
                                    this.lobby.sendToAll(p.getName() + " lost.");
                                    this.lobby.sendToAll("");
                                }
                                else if(p.getClient().isClosed()){
                                    this.lobby.sendToAll(p.getName() + " disconnected.");
                                    this.lobby.sendToAll("");
                                }
                            }

                            this.hasFinished = true;
                            return;
                        }



                        this.lobby.sendToAll("Turn of " + player.getName() + ", they have " + player.getDices().size() + " dices left.");
                        this.lobby.sendToAll("");

                        player.sendToThis("[--YOUR TURN--]");
                        player.sendToThis(player.getName() + "'s Dices: " + player.getStringDices());
                        player.sendToThis("");

                        if (this.currentBet == null) {
                            while(!this.lobby.getPlayers().isEmpty()) {
                                player.sendToThis("[--START BET--]");
                                player.sendToThis("");
                                player.sendToThis("askStartBet");

                                this.startWaiting();

                                String choice = player.getPlayerInteraction();

                                if (choice == null) {
                                    break;
                                }

                                String[] bet = choice.split(";");

                                String diceValue = bet[0].replace("diceValue:", "");
                                String diceNumber = bet[1].replace("diceNumber:", "");

                                if (this.setStartBet(player, Integer.parseInt(diceValue), Integer.parseInt(diceNumber))) {
                                    break;
                                }
                            }

                            if(currentBet != null){
                                this.lobby.sendToAll(player.getName() + " made the bet: " + this.currentBet.toString());
                            }
                        }
                        else {
                            this.lobby.sendToAll("Current Bet: " + this.currentBet.toString());

                            while(!this.lobby.getPlayers().isEmpty()) {
                                player.sendToThis("askAction");

                                LinkedList<Thread> handlers = new LinkedList<Thread>();

                                if(lobby.getSettings().canSockIt()){
                                    for(Player p : lobby.getPlayers()){
                                        if(!p.equals(player)){
                                            Thread sockItHandler = new Thread(new SockItHandler(p, this));
                                            sockItHandler.start();
                                            handlers.add(sockItHandler);
                                        }
                                    }
                                }

                                this.startWaiting();

                                String choice = player.getPlayerInteraction();

                                if(lobby.getSettings().canSockIt()){
                                    if(!sockIt){
                                        for(Thread thread : handlers){
                                            thread.stop();
                                        }
                                    }
                                    else {
                                        player.sendToThis("Someone called Sock It, so your bet was skipped.");
                                        break;
                                    }
                                }


                                if (choice == null) {
                                    break;
                                }

                                if (choice.equals("1")) {
                                    this.lobby.sendToAll("[--DOUBT--]");
                                    this.lobby.sendToAll("");
                                    this.lobby.sendToAll(player.getName() + " made a Doubt!");
                                    this.lobby.sendToAll(player.getName() + " doubts that there are " + this.currentBet.getDiceNumber() + " dices of value " + this.currentBet.getDiceValue() + ".");

                                    boolean doubt = this.doubt();

                                    this.lobby.sendToAll("");

                                    for(Player p : lobby.getPlayers()){
                                        this.lobby.sendToAll("Dices of: " + p.getName());
                                        this.lobby.sendToAll(p.getStringDices());
                                        this.lobby.sendToAll("");
                                    }

                                    if (!doubt) {
                                        this.lobby.sendToAll("Doubt Lost. " + player.getName() + " lost a dice.");

                                        player.removeDice();

                                        if (!player.hasDices()) {
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll(player.getName() + " lost the network.game.");
                                            this.playersAlive--;
                                        }
                                        else{
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll("As " + player.getName() + " lost a dice, they start the next round.");
                                            if(player.getDices().size() == 1){
                                                this.lobby.sendToAll("");
                                                this.lobby.sendToAll(player.getName() + " is PALIFIC!");
                                                palific = true;
                                                palificRound = round;
                                            }
                                            i--;
                                        }
                                    }
                                    else {
                                        this.lobby.sendToAll("Doubt Won. " + this.currentBet.getPlayer().getName() + " lost a dice.");

                                        this.currentBet.getPlayer().removeDice();

                                        if (!this.currentBet.getPlayer().hasDices()) {
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll(this.currentBet.getPlayer().getName() + " lost the network.game.");
                                            this.playersAlive--;
                                        }
                                        else{
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll("As " + this.currentBet.getPlayer().getName() + " lost a dice, they start the next round.");
                                            if(this.currentBet.getPlayer().getDices().size() == 1){
                                                this.lobby.sendToAll("");
                                                this.lobby.sendToAll(this.currentBet.getPlayer().getName() + " is PALIFIC!");
                                                palific = true;
                                                palificRound = round;
                                            }
                                            i = lobby.getPlayers().indexOf(this.currentBet.getPlayer());
                                        }
                                    }

                                    this.nextRound();

                                    break;
                                }
                                else if (choice.equals("2")) {
                                    while(!this.lobby.getPlayers().isEmpty()) {
                                        player.sendToThis("[--NEW BET--]");
                                        player.sendToThis("");
                                        player.sendToThis("1. Change Dice Value (Currently: " + this.currentBet.getDiceValue() + ")");
                                        player.sendToThis("2. Change Dice Number (Currently: " + this.currentBet.getDiceNumber() + ")");

                                        player.sendToThis("askNewBet");

                                        this.startWaiting();

                                        String newBet = player.getPlayerInteraction();

                                        if (newBet.contains("diceValue:")) {
                                            if (this.setNewDiceValue(player, Integer.parseInt(newBet.replace("diceValue:", "")))) {
                                                this.lobby.sendToAll(player.getName() + " made the bet: " + this.currentBet.toString());
                                                break;
                                            }
                                        }
                                        else if (newBet.contains("diceNumber:")) {
                                            if(this.setNewDiceNumber(player, Integer.parseInt(newBet.replace("diceNumber:", "")))){
                                                this.lobby.sendToAll(player.getName() + " made the bet: " + this.currentBet.toString());
                                                break;
                                            }
                                        }
                                    }

                                    if(sockIt && sockItUser !=  null){
                                        this.lobby.sendToAll("");
                                        this.lobby.sendToAll("[--SOCK IT--]");
                                        this.lobby.sendToAll(sockItUser.getName() + " called a SOCK IT!");
                                        this.lobby.sendToAll("");

                                        boolean result = sockIt();

                                        if(!result){
                                            this.lobby.sendToAll("Sock It Lost. " + player.getName() + " lost a dice.");

                                            player.removeDice();

                                            if (!player.hasDices()) {
                                                this.lobby.sendToAll("");
                                                this.lobby.sendToAll(player.getName() + " lost the network.game.");
                                                this.playersAlive--;
                                            }
                                            else{
                                                this.lobby.sendToAll("");
                                                this.lobby.sendToAll("As " + player.getName() + " lost a dice, they start the next round.");
                                                if(player.getDices().size() == 1){
                                                    this.lobby.sendToAll("");
                                                    this.lobby.sendToAll(player.getName() + " is PALIFIC!");
                                                    palific = true;
                                                    palificRound = round;
                                                }
                                                i--;
                                            }
                                        }
                                        else{
                                            this.lobby.sendToAll("Sock It Won! " + player.getName() + " gained a dice!");

                                            player.addDice(lobby.getSettings().useJollies());
                                        }

                                        this.nextRound();
                                    }

                                    break;
                                }

                                player.sendToThis("Not a choice that you can make.");
                            }
                        }

                        this.lobby.sendToAll("");
                        this.lobby.sendToAll("End of " + player.getName() + "'s turn.");
                        this.lobby.sendToAll("");
                    }
                }
                catch(IOException e){
                    this.lobby.leaveLobby(player);
                }
                catch (InterruptedException e) {
                    System.out.println("GameManager encountered a problem. Closing.");
                }
            }
        }
    }

    public boolean hasFinished() {
        return this.hasFinished;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void startWaiting() throws InterruptedException {
        synchronized(this) {
            this.wait();
        }
    }

    public void stopWaiting() {
        synchronized(this) {
            this.notify();
        }
    }

    public boolean setStartBet(Player player, int diceValue, int diceNumber) throws IOException {
        if (diceValue >= 2 && diceValue <= 6) {
            if (diceNumber <= 0) {
                player.sendToThis("Not a viable dice number, must be greater than 0.");
                return false;
            }
            else {
                this.currentBet = new Bet(player, diceValue, diceNumber);
                return true;
            }
        }
        else {
            player.sendToThis("Not a viable dice value, must be greater than 2 and lower than 6.");
            return false;
        }
    }

    public boolean setNewDiceValue(Player player, int newDiceValue) throws IOException {
        int minDiceValue = minDiceValue();
        if (newDiceValue >= minDiceValue && newDiceValue <= 6 && ((this.lobby.getSettings().useJollies() && newDiceValue == 1) || newDiceValue > this.currentBet.getDiceValue()) && !palific) {
            this.currentBet = new Bet(player, newDiceValue, this.currentBet.getDiceNumber());
            return true;
        }
        else {
            if(palific){
                player.sendToThis("Cannot change the dice value when there's a PALIFIC player.");
            }
            else{
                player.sendToThis("Not a viable value, must be greater than 2 and lower than 6. It also must be bigger than the current bet's dice value (" + this.currentBet.diceValueString() + ").");
            }
            return false;
        }
    }

    public boolean setNewDiceNumber(Player player, int newDiceNumber) throws IOException {
        int min = newDiceNumber == 1 ? this.currentBet.getDiceNumber()/2 : this.currentBet.getDiceNumber();
        if (newDiceNumber <= min) {
            player.sendToThis("Not a viable value, must be  bigger than the current bet's dice number (" + min + ")");
            return false;
        } else {
            this.currentBet = new Bet(player, this.currentBet.getDiceValue(), newDiceNumber);
            return true;
        }
    }

    public boolean doubt(){
        boolean value = false;

        int diceCount = countDices();

        showPlayerDices();

        if (this.currentBet.getDiceNumber() > diceCount) {
            value = true;
        }

        this.lobby.sendToAll("");
        this.lobby.sendToAll("Dices with value (" + this.currentBet.getDiceValue() + ") found: " + diceCount);
        this.lobby.sendToAll("");
        return value;
    }
    public boolean sockIt(){
        boolean value = false;

        int diceCount = countDices();

        showPlayerDices();

        if (this.currentBet.getDiceNumber() == diceCount) {
            value = true;
        }

        this.lobby.sendToAll("");
        this.lobby.sendToAll("Dices with value (" + this.currentBet.getDiceValue() + ") found: " + diceCount);
        this.lobby.sendToAll("");
        return value;
    }

    public int countDices(){
        int diceCount = 0;
        for(Player p: lobby.getPlayers()) {
            for(Dice dice : p.getDices()) {
                if(dice.getValue() == this.currentBet.getDiceValue() || (!palific && dice.getValue() == 1)){
                    diceCount++;
                }
            }
        }
        return diceCount;
    }

    public void showPlayerDices() {
        for(Player player : lobby.getPlayers()){
            this.lobby.sendToAll(player.getName() + ": " + player.getStringDices());
            this.lobby.sendToAll("");
        }
    }

    public void setSockIt(Player player) {
        if(!sockIt){
            sockItUser = player;
            sockIt = true;
        }
    }

    public int minDiceValue(){
        if(this.lobby.getSettings().useJollies() && !palific){
            return 1;
        }
        else{
            return 2;
        }
    }

    public void nextRound(){
        this.currentBet = null;
        this.lobby.sendToAll("");
        this.lobby.sendToAll("RE-ROLLING ALL PLAYER'S DICES");
        this.lobby.sendToAll("");

        for(Player p : lobby.getPlayers()){
            p.rollAll();
        }

        if(palific && palificRound!=round){
            palific = false;
        }

        this.round++;

        this.lobby.sendToAll("Round " + this.round);
    }
}
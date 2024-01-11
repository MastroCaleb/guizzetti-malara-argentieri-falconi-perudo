package game.manager;

import game.bet.Bet;
import game.dices.Dice;
import game.player.Player;
import java.util.Iterator;
import network.server.lobbies.Lobby;

public class GameManager implements Runnable {
    private Bet currentBet = null;
    private int round = 0;
    private Lobby lobby;
    private int playersAlive = 0;
    private boolean hasFinished = false;

    public GameManager(Lobby lobby) {
        this.lobby = lobby;
    }

    public void run() {
        this.playersAlive = this.lobby.getPlayers().size();
        this.lobby.sendToAll("Round " + this.round);

        while(true) {
            for(Player player : lobby.getPlayers()) {
                try {
                    if (player.hasDices()) {

                        if (this.playersAlive == 1 || this.lobby.getPlayers().size() == 1) {
                            this.lobby.sendToAll("[--WINNER--]");
                            this.lobby.sendToAll("");
                            this.lobby.sendToAll(player.getName() + " has won the game.");
                            this.lobby.sendToAll("");
                            this.hasFinished = true;
                            return;
                        }

                        this.lobby.sendToAll("Turn of " + player.getName());

                        player.sendToThis(player.getName() + ": " + player.getStringDices());

                        if (this.currentBet == null) {
                            while(true) {
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

                                this.lobby.sendToAll(player.getName() + " made the bet: " + this.currentBet.toString());
                            }
                        }
                        else {
                            this.lobby.sendToAll("Current Bet: " + this.currentBet.toString());

                            while(true) {
                                player.sendToThis("askAction");

                                this.startWaiting();

                                String choice = player.getPlayerInteraction();

                                if (choice == null) {
                                    break;
                                }

                                if (choice.equals("1")) {

                                    this.lobby.sendToAll("[--DOUBT--]");
                                    this.lobby.sendToAll("");
                                    this.lobby.sendToAll(player.getName() + " made a Doubt!");
                                    this.lobby.sendToAll(player.getName() + " doubts that there are " + this.currentBet.getDiceNumber() + " dices of value " + this.currentBet.getDiceValue() + ".");

                                    boolean doubt = this.doubt(player);

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
                                            this.lobby.sendToAll(player.getName() + " lost the game.");
                                            this.playersAlive--;
                                        }
                                    }
                                    else {
                                        this.lobby.sendToAll("Doubt Won. " + this.currentBet.getPlayer().getName() + " lost a dice.");

                                        this.currentBet.getPlayer().removeDice();

                                        if (!this.currentBet.getPlayer().hasDices()) {
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll(this.currentBet.getPlayer().getName() + " lost the game.");
                                            this.playersAlive--;
                                        }
                                    }

                                    this.currentBet = null;
                                    this.lobby.sendToAll("");
                                    this.lobby.sendToAll("RE-ROLLING ALL PLAYER'S DICES");
                                    this.lobby.sendToAll("");

                                    for(Player p : lobby.getPlayers()){
                                        p.rollAll();
                                    }

                                    this.round++;

                                    this.lobby.sendToAll("Round " + this.round);
                                    this.lobby.sendToAll("");

                                    break;
                                }

                                if (choice.equals("2")) {
                                    while(true) {
                                        player.sendToThis("[--NEW BET--]");
                                        player.sendToThis("");
                                        player.sendToThis("1. Change Dice Value (Currently: " + this.currentBet.getDiceValue() + ")");
                                        player.sendToThis("2. Change Dice Number (Currently: " + this.currentBet.getDiceNumber() + ")");

                                        player.sendToThis("askNewBet");

                                        this.startWaiting();

                                        String newBet = player.getPlayerInteraction();

                                        if (newBet == null) {
                                            break;
                                        }

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
                                }

                                player.sendToThis("Not a choice that you can make.");
                            }
                        }

                        this.lobby.sendToAll("");
                        this.lobby.sendToAll("End of " + player.getName() + "'s turn.");
                        this.lobby.sendToAll("");
                    }
                } catch (InterruptedException var7) {
                    System.out.println("GameManager encountered a problem. Closing.");
                }
            }
        }
    }

    public boolean hasFinished() {
        return this.hasFinished;
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

    public boolean setStartBet(Player player, int diceValue, int diceNumber) {
        if (diceValue >= 2 && diceValue <= 6) {
            if (diceNumber <= 0) {
                player.sendToThis("Not a viable dice number, must be greater than 0.");
                return false;
            } else {
                this.currentBet = new Bet(player, diceValue, diceNumber);
                return true;
            }
        } else {
            player.sendToThis("Not a viable dice value, must be greater than 2 and lower than 6.");
            return false;
        }
    }

    public boolean setNewDiceValue(Player player, int newDiceValue) {
        if (newDiceValue >= 2 && newDiceValue <= 6 && newDiceValue > this.currentBet.getDiceValue()) {
            this.currentBet = new Bet(player, newDiceValue, this.currentBet.getDiceNumber());
            return true;
        } else {
            player.sendToThis("Not a viable value, must be greater than 2 and lower than 6. It also must be bigger than the current bet's dice value (" + this.currentBet.getDiceValue() + ").");
            return false;
        }
    }

    public boolean setNewDiceNumber(Player player, int newDiceNumber) {
        if (newDiceNumber <= this.currentBet.getDiceNumber()) {
            player.sendToThis("Not a viable value, must be  bigger than the current bet's dice number (" + this.currentBet.getDiceNumber() + ")");
            return false;
        } else {
            this.currentBet = new Bet(player, this.currentBet.getDiceValue(), newDiceNumber);
            return true;
        }
    }

    public boolean doubt(Player player) {
        boolean value = false;
        int diceCount = 0;
        Iterator var4 = this.lobby.getPlayers().iterator();

        label31:
        while(var4.hasNext()) {
            Player p = (Player)var4.next();
            Iterator var6 = p.getDices().iterator();

            while(true) {
                Dice dice;
                do {
                    if (!var6.hasNext()) {
                        continue label31;
                    }

                    dice = (Dice)var6.next();
                } while(dice.getValue() != this.currentBet.getDiceValue() && !dice.toString().equals("J"));

                ++diceCount;
            }
        }

        if (this.currentBet.getDiceNumber() > diceCount) {
            value = true;
        }

        this.lobby.sendToAll("");
        Lobby var10000 = this.lobby;
        int var10001 = this.currentBet.getDiceValue();
        var10000.sendToAll("Dices with value (" + var10001 + ") found: " + diceCount);
        this.lobby.sendToAll("");
        return value;
    }
}
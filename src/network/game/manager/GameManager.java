package network.game.manager;

import network.game.bet.Bet;
import network.game.dice.Dice;
import network.game.player.Player;
import network.packets.bet.BetPacket;
import network.server.lobbies.Lobby;
import network.server.service.SockItHandler;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

import java.io.IOException;
import java.util.LinkedList;

/**
 * This class manages the Game in a lobby.
 */
@SuppressWarnings("deprecation")
public class GameManager implements Runnable {
    private final Logger LOGGER = new Logger("GameManager");
    private Bet currentBet = null; //The last bet that was made. This is null if the game just started or a new Round started.
    private int round = 1; //Number of rounds.
    private final Lobby lobby; //The lobby that this game was started in.
    private boolean hasFinished = false; //If the game has ended.

    //PALIFIC
    private boolean palific = false; //If there is a palific player.
    private int palificRound = 0; //This regulates if diceValue can be changed (if there's a palific player it shouldn't be changed)

    //SOCK IT
    private Player sockItUser = null; //The player that called Sock It.
    private boolean sockIt = false; //If sock it was called or not.

    public GameManager(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void run() {
        //We get how many players this game started with.
        int playersAlive = this.lobby.getPlayers().size();
        this.lobby.sendToAll("");
        this.lobby.sendToAll("Round " + this.round);
        this.lobby.sendToAll("");

        //Start game.
        while(true) {
            for(int i = 0; i<lobby.getPlayers().size(); i++) {
                Player player = lobby.getPlayers().get(i); //Turn of this player.
                try {
                    //If this player has dices he can play, otherwise he is spectating.
                    if (player.hasDices()) {
                        //If there's one player alive or the lobby only has one player left the game ends.
                        if (playersAlive == 1 || this.lobby.getPlayers().size() == 1) {
                            this.lobby.sendToAll("[--GAME HAS ENDED--]");
                            this.lobby.sendToAll("");
                            this.lobby.sendToAll(player.getName() + " won the game!");
                            this.lobby.sendToAll("");

                            LinkedList<Player> losers = getLosers();
                            LinkedList<Player> disconnected = lobby.getDisconnectedPlayers();

                            //We list every player that either lost or has disconnected (shame on them).

                            if(!losers.isEmpty()){
                                this.lobby.sendToAll("[--LOSERS--]");
                                for(Player p : losers){
                                    this.lobby.sendToAll("- " + p.getName());
                                }
                            }

                            this.lobby.sendToAll("");

                            if(!disconnected.isEmpty()){
                                this.lobby.sendToAll("[--DISCONNECTED--]");
                                for(Player p : disconnected){
                                    this.lobby.sendToAll("- " + p.getName());
                                }

                                this.hasFinished = true;
                            }

                            return;
                        }

                        this.lobby.sendToAll("Turn of " + player.getName() + ", they have " + player.getDices().size() + " dices left.");
                        this.lobby.sendToAll("");

                        player.sendToThis("[--YOUR TURN--]");
                        player.sendToThis(player.getName() + "'s Dices: " + player.getStringDices());
                        player.sendToThis("");

                        //If the current bet is null we are at the start of the game or in a new round.
                        if (this.currentBet == null) {
                            while(!this.lobby.getPlayers().isEmpty()) {
                                player.sendToThis("[--START BET--]");
                                player.sendToThis("");

                                player.ask("StartBet");

                                this.startWaiting();

                                String interaction = player.getPlayerInteraction();

                                //If interaction is null the player disconnected.
                                if (interaction == null) {
                                    break;
                                }

                                //We receive the packet and check if it is a usable bet.
                                try{
                                    BetPacket packet = new BetPacket(interaction);

                                    //If it's usable we go to the next turn, if not we repeat the loop.
                                    if (this.setStartBet(player, packet.getDiceValue(), packet.getDiceNumber())) {
                                        break;
                                    }
                                }
                                catch(NoSuchFieldException | IllegalAccessException ignored){} //Wrong packet received, let's restart the loop.
                            }

                            //We show all players the new bet.
                            if(currentBet != null){
                                this.lobby.sendToAll(player.getName() + " made the bet: " + this.currentBet.toString());
                            }
                        }
                        else {
                            this.lobby.sendToAll("Current Bet: " + this.currentBet);

                            //Ask the player weather he wants to doubt or change the bet.
                            while(!this.lobby.getPlayers().isEmpty()) {

                                player.sendToThis("[--DOUBT OR BET--]");
                                player.sendToThis("");
                                player.sendToThis("1. Doubt");
                                player.sendToThis("2. Bet Again");

                                player.ask("Action");

                                //These threads handle the SockItHandlers of each player. But we start them only if the rule is set to true.

                                LinkedList<Thread> handlers = new LinkedList<>();

                                if(lobby.getSettings().canSockIt()){
                                    for(Player p : lobby.getPlayers()){
                                        if(!p.equals(player) && p.hasDices()){
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
                                        //If someone called sock it, skip this player's turn.
                                        player.sendToThis("Someone called Sock It, so your bet was skipped.");
                                        for(Thread thread : handlers){
                                            thread.stop();
                                        }
                                        break;
                                    }
                                }

                                //If choice is null the player disconnected.
                                if (choice == null) {
                                    break;
                                }

                                //If players chooses 1 we call a doubt and check each player's dices.
                                if (choice.equals("1")) {
                                    this.lobby.sendToAll("[--DOUBT--]");
                                    this.lobby.sendToAll("");
                                    this.lobby.sendToAll(player.getName() + " made a Doubt!");
                                    this.lobby.sendToAll(player.getName() + " doubts that there are " + this.currentBet.diceNumber() + " dices of value " + this.currentBet.diceValue() + ".");

                                    boolean doubt = this.doubt(); //If true the doubt is won, if false it's lost.

                                    this.lobby.sendToAll("");

                                    //If the doubt is lost we remove a dice from the player that called it.
                                    if (!doubt) {
                                        this.lobby.sendToAll("Doubt Lost. " + player.getName() + " lost a dice.");

                                        player.removeDice();

                                        if (!player.hasDices()) {
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll(player.getName() + " lost the game.");
                                            playersAlive--;
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
                                    //If the doubt is won we remove a dice from the player that made the last bet.
                                    else {
                                        this.lobby.sendToAll("Doubt Won. " + this.currentBet.player().getName() + " lost a dice.");

                                        this.currentBet.player().removeDice();

                                        if (!this.currentBet.player().hasDices()) {
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll(this.currentBet.player().getName() + " lost the game.");
                                            playersAlive--;
                                        }
                                        else{
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll("As " + this.currentBet.player().getName() + " lost a dice, they start the next round.");
                                            if(this.currentBet.player().getDices().size() == 1){
                                                this.lobby.sendToAll("");
                                                this.lobby.sendToAll(this.currentBet.player().getName() + " is PALIFIC!");
                                                palific = true;
                                                palificRound = round;
                                            }
                                            System.out.println("Next round should be given to: " +  this.currentBet.player().getName());
                                            i = lobby.getPlayers().indexOf(this.currentBet.player()) - 1;
                                        }
                                    }

                                    this.nextRound();

                                    break;
                                }
                                //If choice is 2 we ask the player what he would like to change about the bet.
                                else if (choice.equals("2")) {
                                    while(!this.lobby.getPlayers().isEmpty()) {
                                        player.sendToThis("[--NEW BET--]");
                                        player.sendToThis("");
                                        player.sendToThis("1. Change Dice Value (Currently: " + this.currentBet.diceValue() + ")");
                                        player.sendToThis("2. Change Dice Number (Currently: " + this.currentBet.diceNumber() + ")");

                                        player.ask("NewBet");

                                        this.startWaiting();

                                        String newBet = player.getPlayerInteraction();

                                        //We check the changed dice value.
                                        if (newBet.contains("diceValue:")) {
                                            //if the new dice value is a usable one we change the current bet and go to the next turn. Otherwise, we repeat the loop.
                                            if (this.setNewDiceValue(player, Integer.parseInt(newBet.replace("diceValue:", "")))) {
                                                this.lobby.sendToAll(player.getName() + " made the bet: " + this.currentBet.toString());
                                                break;
                                            }
                                        }
                                        //We check the changed dice number.
                                        else if (newBet.contains("diceNumber:")) {
                                            //if the new dice number is a usable one we change the current bet and go to the next turn. Otherwise, we repeat the loop.
                                            if(this.setNewDiceNumber(player, Integer.parseInt(newBet.replace("diceNumber:", "")))){
                                                this.lobby.sendToAll(player.getName() + " made the bet: " + this.currentBet.toString());
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                player.sendToThis("Not a choice that you can make.");
                            }

                            //If sock it was called and the player that made the sock it isn't null we continue.
                            if(sockIt && sockItUser !=  null) {
                                this.lobby.sendToAll("");
                                this.lobby.sendToAll("[--SOCK IT--]");
                                this.lobby.sendToAll(sockItUser.getName() + " called a SOCK IT!");
                                this.lobby.sendToAll("");

                                boolean result = sockIt(); //If true the sock it is won. If false the sock it is lost.

                                //If the player lost the Sock It, we remove a dice.
                                if (!result) {
                                    this.lobby.sendToAll("Sock It Lost. " + player.getName() + " lost a dice.");

                                    player.removeDice();

                                    if (!player.hasDices()) {
                                        this.lobby.sendToAll("");
                                        this.lobby.sendToAll(player.getName() + " lost the game.");
                                        playersAlive--;
                                    }
                                    else {
                                        this.lobby.sendToAll("");
                                        this.lobby.sendToAll("As " + player.getName() + " lost a dice, they start the next round.");
                                        if (player.getDices().size() == 1) {
                                            this.lobby.sendToAll("");
                                            this.lobby.sendToAll(player.getName() + " is PALIFIC!");
                                            palific = true;
                                            palificRound = round;
                                        }
                                        i--;
                                    }
                                }
                                //If the player won the Sock It, we add a dice.
                                else {
                                    this.lobby.sendToAll("Sock It Won! " + player.getName() + " gained a dice!");

                                    player.addDice(lobby.getSettings().useJollies());
                                }

                                this.nextRound();
                            }
                        }

                        this.lobby.sendToAll("");
                        this.lobby.sendToAll("End of " + player.getName() + "'s turn.");
                        this.lobby.sendToAll("");
                    }
                }
                catch(IOException e){
                    LOGGER.log(LoggerLevel.ERROR, "Player encountered an exception and is being disconnected.");
                    this.lobby.leaveLobby(player);
                }
                catch (InterruptedException e) {
                    LOGGER.log(LoggerLevel.ERROR, "The GameManager encountered a Thread error and is stopping.");
                    return;
                }
            }
        }
    }

    /**
     * @return True if the game ended. False if the game is still going.
     */
    public boolean hasFinished() {
        return this.hasFinished;
    }

    /**
     * @return The lobby that the game is running on.
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Stops the thread temporarily.
     *
     */
    public void startWaiting() throws InterruptedException {
        synchronized(this) {
            this.wait();
        }
    }

    /**
     * Restarts the thread after it stopped.
     */
    public void stopWaiting() {
        synchronized(this) {
            this.notify();
        }
    }

    /**
     * This sets the start bet when the game starts or when a new round starts.
     *
     * @param player The player that made the bet.
     * @param diceValue The dice value the player bet.
     * @param diceNumber The dice number the player bet.
     * @return True if the bet is usable. False if a condition isn't met.
     */
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

    /**
     * Changes the current bet's dice value to a new one.
     *
     * @param player The player that made this change.
     * @param newDiceValue The new dice value.
     * @return True if the bet is usable. False if a condition isn't met.
     */
    public boolean setNewDiceValue(Player player, int newDiceValue) throws IOException {
        int minDiceValue = minDiceValue();
        if (newDiceValue >= minDiceValue && newDiceValue <= 6 && ((this.lobby.getSettings().useJollies() && newDiceValue == 1) || newDiceValue > this.currentBet.diceValue()) && !palific) {
            this.currentBet = new Bet(player, newDiceValue, this.currentBet.diceNumber());
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

    /**
     * Changes the current bet's dice number to a new one.
     *
     * @param player The player that made this change.
     * @param newDiceNumber The new dice number.
     * @return True if the bet is usable. False if a condition isn't met.
     */
    public boolean setNewDiceNumber(Player player, int newDiceNumber) throws IOException {
        int min = newDiceNumber == 1 ? this.currentBet.diceNumber()/2 : this.currentBet.diceNumber();
        if (newDiceNumber <= min) {
            player.sendToThis("Not a viable value, must be  bigger than the current bet's dice number (" + min + ")");
            return false;
        } else {
            this.currentBet = new Bet(player, this.currentBet.diceValue(), newDiceNumber);
            return true;
        }
    }

    /**
     * Makes a doubt.
     *
     * @return True if doubt is won. False if it's lost.
     */
    public boolean doubt(){
        boolean value = false;

        int diceCount = countDices();

        this.lobby.sendToAll("Dices on the table: ");
        showPlayerDices();

        if (this.currentBet.diceNumber() > diceCount) {
            value = true;
        }

        this.lobby.sendToAll("");
        this.lobby.sendToAll("Dices with value (" + this.currentBet.diceValue() + ") found: " + diceCount);
        this.lobby.sendToAll("");
        return value;
    }

    /**
     * Makes a sock it.
     *
     * @return True if sock it is won. False if it's lost.
     */
    public boolean sockIt(){
        boolean value = false;

        int diceCount = countDices();

        showPlayerDices();

        if (this.currentBet.diceNumber() == diceCount) {
            value = true;
        }

        this.lobby.sendToAll("");
        this.lobby.sendToAll("Dices with value (" + this.currentBet.diceValue() + ") found: " + diceCount);
        this.lobby.sendToAll("");
        return value;
    }

    /**
     * @return The number of dices in total that have the same dice value as the last bet.
     */
    public int countDices(){
        int diceCount = 0;
        for(Player p: lobby.getPlayers()) {
            for(Dice dice : p.getDices()) {
                if(dice.getValue() == this.currentBet.diceValue() || (!palific && dice.getValue() == 1)){
                    diceCount++;
                }
            }
        }
        return diceCount;
    }

    /**
     * Shows all players dices.
     */
    public void showPlayerDices() {
        for(Player player : lobby.getPlayers()){
            if(player.hasDices()){
                this.lobby.sendToAll(player.getName() + ": " + player.getStringDices());
                this.lobby.sendToAll("");
            }
        }
    }

    /**
     * Shows all players the count of dices of each player.
     */
    public void showPlayerDiceCount() {
        for(Player player : lobby.getPlayers()){
            if(player.hasDices()){
                this.lobby.sendToAll(player.getName() + ": " + player.getDices().size());
                this.lobby.sendToAll("");
            }
        }
    }

    /**
     * Sets if someone called a Sock It and which player called it.
     * @param player The player that called Sock It.
     */
    public void setSockIt(Player player) {
        if(!sockIt){
            sockItUser = player;
            sockIt = true;
        }
    }

    /**
     * @return Gets the number of dices of the player with the least dices that is still playing.
     */
    public int minDiceValue(){
        if(this.lobby.getSettings().useJollies() && !palific){
            return 1;
        }
        else{
            return 2;
        }
    }

    /**
     * Goes to next round.
     */
    public void nextRound() {
        this.currentBet = null;
        this.lobby.sendToAll("");
        this.lobby.sendToAll("RE-ROLLING ALL PLAYER'S DICES");
        this.lobby.sendToAll("");

        //Roll all player dices.
        for(Player p : lobby.getPlayers()){
            p.rollAll();
        }

        showPlayerDiceCount();

        if(palific && palificRound!=round){
            palific = false;
        }

        this.round++;

        this.lobby.sendToAll("Round " + this.round);
    }

    /**
     * @return The list of players that lost the game.
     */
    public LinkedList<Player> getLosers(){
        LinkedList<Player> losers = new LinkedList<>();

        for (Player player : lobby.getPlayers()){
            if(!player.hasDices()){
                losers.add(player);
            }
        }

        return losers;
    }
}
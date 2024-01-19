package network.game.player;

import main.Main;
import network.game.dice.Dice;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import network.server.lobbies.Lobby;

public class Player {
    private final String name; //This player's name.
    private final Socket client; //This player's socket.
    private LinkedList<Dice> dices = new LinkedList<>(); //This player's dices.
    private String playerInteraction; //This player's last sent packet.

    public Player(String name, Socket client) {
        this.name = name;
        this.client = client;
    }

    /**
     * @return True if the player has at least one dice. False if the player has no dices.
     */
    public boolean hasDices() {
        return !this.dices.isEmpty();
    }

    /**
     * Rolls all the player's dices.
     */
    public void rollAll() {
        for(Dice dice : dices) {
            dice.roll();
        }
    }

    /**
     * Removes all the player's dices.
     */
    public void removeAllDices() {
        this.dices = new LinkedList<>();
    }

    /**
     * Removes only one dice from the player.
     */
    public void removeDice() {
        this.dices.remove();
    }

    /**
     * Adds a dice to the player.
     * @param jollies If jollies are used in the lobby or not.
     */
    public void addDice(boolean jollies) {this.dices.add(new Dice(jollies));}

    /**
     * Sets up the player with the max number of dices provided by the lobby.
     * @param lobby The lobby in which the player is in.
     */
    public void setupPlayer(Lobby lobby) {
        for(int i = 0; i < lobby.getSettings().getMaxDices(); ++i) {
            this.dices.add(new Dice(lobby.getSettings().useJollies()));
        }
    }

    /**
     * Resets the player with the same number of dices as the player with the least number of dices in the lobby.
     * @param lobby The lobby in which the player is in.
     * @param numberOfDices The least number of dices in the lobby.
     */
    public void reSetupPlayer(Lobby lobby, int numberOfDices) {
        for (int i = 0; i < numberOfDices; ++i) {
            this.dices.add(new Dice(lobby.getSettings().useJollies()));
        }
    }

    /**
     * @return The list of dices.
     */
    public LinkedList<Dice> getDices() {
        return this.dices;
    }

    /**
     * @return The list of dices as a String.
     */
    public String getStringDices() {
        StringBuilder value = new StringBuilder();
        int times = 1;

        for(Dice dice : dices) {
            if (times == dices.size()) {
                value.append(dice.toString());
            }
            else {
                value.append(dice.toString()).append(" | ");
            }
            times++;
        }

        return value.toString();
    }

    /**
     * @return The list of dices as a String, but the values are hidden with a "?".
     */
    public String getHiddenStringDices(){
        StringBuilder value = new StringBuilder();
        int times = 1;

        for(Dice ignored : dices) {
            if (times == dices.size()) {
                value.append("?");
            }
            else {
                value.append("?").append(" | ");
            }
            times++;
        }

        return value.toString();
    }

    public String getName() {
        return this.name;
    }

    public Socket getClient() {
        return this.client;
    }

    public String getPlayerInteraction() {
        return this.playerInteraction;
    }

    public void setPlayerInteraction(String playerInteraction) {
        this.playerInteraction = playerInteraction;
    }

    /**
     * Sends a message to the player.
     * @param message The contents of the message.
     */
    public void sendToThis(String message) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(this.client.getOutputStream());
        outputStream.writeUTF(message);
    }

    /**
     * Sends a packet or command to the player.
     * @param packet The packet or command to send.
     */
    public void ask(String packet) throws IOException {
        sendToThis("ask" + packet);
    }

    /**
     * Cleans the player's console (works only if the OS's terminal is open.)
     * @throws IOException
     */
    public void clean() throws IOException {
        ask("Clean");
    }
}

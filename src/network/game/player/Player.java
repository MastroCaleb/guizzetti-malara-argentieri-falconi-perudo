package network.game.player;

import network.game.dice.Dice;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import network.server.lobbies.Lobby;

public class Player {
    private final String name;
    private final Socket client;
    private LinkedList<Dice> dices = new LinkedList<>();
    private String playerInteraction;

    public Player(String name, Socket client) {
        this.name = name;
        this.client = client;
    }
    public boolean hasDices() {
        return !this.dices.isEmpty();
    }

    public void rollAll() {
        for(Dice dice : dices) {
            dice.roll();
        }
    }

    public void removeAllDices() {
        this.dices = new LinkedList<>();
    }
    public void removeDice() {
        this.dices.remove();
    }
    public void setupPlayer(Lobby lobby) {
        for(int i = 0; i < lobby.getSettings().getMaxDices(); ++i) {
            this.dices.add(new Dice(lobby.getSettings().useJollies()));
        }
    }
    public void addDice(boolean jollies) {this.dices.add(new Dice(jollies));}

    public LinkedList<Dice> getDices() {
        return this.dices;
    }

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

    public void sendToThis(String message) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(this.client.getOutputStream());
        outputStream.writeUTF(message);
    }
}

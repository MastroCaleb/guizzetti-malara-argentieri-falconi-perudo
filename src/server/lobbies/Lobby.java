package server.lobbies;

import game.player.Player;

import java.util.LinkedList;

public class Lobby implements Runnable{

    private LobbySettings lobbySettings;
    private LinkedList<Player> players = new LinkedList<Player>();
    public Lobby(LobbySettings lobbySettings){
        this.lobbySettings = lobbySettings;
    }

    @Override
    public void run(){

    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public String playerCount(){
        return "(" + players.size() + "/" + getMaxPlayers() + ")";
    }

    public String getName(){
        return lobbySettings.getLobbyName();
    }
    public boolean isPublic(){
        return lobbySettings.isPublic();
    }
    public int getMaxPlayers(){
        return lobbySettings.getMaxPlayers();
    }
    public int getMaxDices(){
        return lobbySettings.getMaxDices();
    }

}

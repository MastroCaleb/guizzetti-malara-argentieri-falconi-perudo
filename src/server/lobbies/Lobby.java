package server.lobbies;

import game.players.server.ServerPlayer;

import java.net.Socket;
import java.util.LinkedList;

public class Lobby implements Runnable{

    private LobbySettings lobbySettings;
    private LinkedList<ServerPlayer> players = new LinkedList<ServerPlayer>();
    public Lobby(LobbySettings lobbySettings){
        this.lobbySettings = lobbySettings;
    }

    @Override
    public void run(){

    }

    public void addPlayer(){
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

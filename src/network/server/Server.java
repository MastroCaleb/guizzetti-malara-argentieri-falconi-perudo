package network.server;

import network.game.player.Player;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;
import network.server.lobbies.Lobby;
import network.server.service.NewPlayerHandler;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

/**
 * Main class of the Server. This manages new Clients.
 */
public class Server implements Runnable {
    private final Logger LOGGER = new Logger("MainServer");
    private final ServerSocket serverSocket; //The server's socket.
    public static LinkedList<Player> players = new LinkedList<Player>(); //List of all players.
    public static LinkedList<Lobby> lobbies = new LinkedList<Lobby>(); //List of all lobbies.

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        System.out.println("Server Started");
        try{
            while(true) {
                Socket client = this.serverSocket.accept(); //Accept new client connection.

                this.LOGGER.log(LoggerLevel.INFO, "A new Client connected. Creating a new Player instance.");

                Player player = new Player("", client);
                (new Thread(new NewPlayerHandler(player))).start(); //Start to handle the new player.
            }
        }
        catch (IOException e) {
            this.LOGGER.log(LoggerLevel.ERROR, "An Exception was caught. Closing the server.");
        }
    }

    /**
     * @param name The name the player wants to use.
     * @return True if the name is already in use. False if not.
     */
    public static boolean nickIsUsed(String name){
        if(players.isEmpty()){
            return false;
        }

        for(Player player : players){
            if(player.getName().equals(name)){
                return true;
            }
        }

        return false;
    }

    /**
     * @param code The code of the lobby.
     * @return An instance of a lobby if found. Null if no lobby was found.
     */
    public static Lobby getLobbyFromCode(String code) {
        Lobby lobbyFound = null;

        for(Lobby lobby : lobbies) {
            if (lobby.getCode().equals(code)) {
                lobbyFound = lobby;
            }
        }

        return lobbyFound;
    }

    /**
     * @return A list of all public lobbies in String form.
     */
    public static String getLobbyList() {
        if (lobbies.isEmpty()) {
            return "No public lobbies available.";
        }
        else {
            String lobbyList = "";
            int count = 0;

            for(Lobby lobby : lobbies) {
                if (lobby.isPublic()) {
                    count++;
                    lobbyList = count + ". " + lobby.getCode() + " " + lobby.playerCount() + "\n";
                }
            }

            if(count == 0){
                return "No public lobbies available.";
            }

            return lobbyList;
        }
    }

    /**
     * @return A new lobby code that isn't used.
     */
    public static String getRandomCode() {
        String code = String.valueOf((new Random()).nextInt(10000, 100000));
        if (!lobbies.isEmpty()) {
            for(Lobby lobby : lobbies) {
                if (lobby.getCode().equals(code)) {
                    code = String.valueOf((new Random()).nextInt(10000, 100000));
                }
            }
        }

        return code;
    }
}


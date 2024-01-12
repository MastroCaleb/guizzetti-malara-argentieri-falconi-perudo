package network.server;

import game.player.Player;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.server.lobbies.Lobby;
import network.server.service.NewPlayerHandler;

public class Server implements Runnable {
    private Logger LOGGER = Logger.getLogger("Server");
    private int port;
    private ServerSocket serverSocket;
    public static LinkedList<Player> players = new LinkedList<Player>();
    public static LinkedList<Lobby> lobbies = new LinkedList<Lobby>();

    public Server(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        System.out.println("Server Started");
        try{
            while(true) {
                Socket client = this.serverSocket.accept();

                DataInputStream inputStream = new DataInputStream(client.getInputStream());

                Player player = new Player("Unnamed", client);
                try{

                    String name = "Unnamed";
                    while (true) {
                        player.sendToThis("askNickName");

                        name = inputStream.readUTF();

                        if (!nickIsUsed(name)) {
                            player = new Player(name, client);
                            break;
                        } else {
                            player.sendToThis("Nickname not available.");
                        }
                    }

                    this.LOGGER.log(Level.INFO, name + " has connected to the server.");

                    players.add(player);

                    (new Thread(new NewPlayerHandler(player))).start();
                }
                catch(SocketException e){
                    players.remove(player);
                }
            }
        }

        catch (IOException e) {
            this.LOGGER.log(Level.SEVERE, "IOException caught. Closing Server.");
        }
    }

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

    public static Lobby getLobbyFromCode(String code) {
        Lobby lobbyFound = null;

        for(Lobby lobby : lobbies) {
            if (lobby.getCode().equals(code)) {
                lobbyFound = lobby;
            }
        }

        return lobbyFound;
    }

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
                    lobbyList = "" + count + ". " + lobby.getCode() + " " + lobby.playerCount() + "\n";
                }
            }

            if(count == 0){
                return "No public lobbies available.";
            }

            return lobbyList;
        }
    }

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


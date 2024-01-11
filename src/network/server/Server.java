package network.server;

import game.player.Player;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public void run() {
        System.out.println("Server Started");
        try{
            while(true) {
                Socket client = this.serverSocket.accept();

                DataInputStream inputStream = new DataInputStream(client.getInputStream());
                String name = inputStream.readUTF();

                this.LOGGER.log(Level.INFO, name + " has connected to the server.");

                Player player = new Player(name, client);
                players.add(player);

                (new Thread(new NewPlayerHandler(player))).start();
            }
        }
        catch (IOException e) {
            this.LOGGER.log(Level.SEVERE, "IOException caught. Closing Server.");
        }
    }

    public static Lobby getLobbyFromCode(String code) {
        Lobby lobbyFound = null;
        Iterator var2 = lobbies.iterator();

        while(var2.hasNext()) {
            Lobby lobby = (Lobby)var2.next();
            if (lobby.getCode().equals(code)) {
                lobbyFound = lobby;
            }
        }

        return lobbyFound;
    }

    public static String getLobbyList() {
        if (lobbies.isEmpty()) {
            return "No public lobbies available.";
        } else {
            String lobbyList = "";
            int count = 0;
            Iterator var2 = lobbies.iterator();

            while(var2.hasNext()) {
                Lobby lobby = (Lobby)var2.next();
                if (lobby.isPublic()) {
                    ++count;
                    lobbyList = "" + count + ". " + lobby.getCode() + " " + lobby.playerCount() + "\n";
                }
            }

            return lobbyList;
        }
    }

    public static String getRandomCode() {
        String code = String.valueOf((new Random()).nextInt(10000, 100000));
        if (!lobbies.isEmpty()) {
            Iterator var1 = lobbies.iterator();

            while(var1.hasNext()) {
                Lobby lobby = (Lobby)var1.next();
                if (lobby.getCode().equals(code)) {
                    code = String.valueOf((new Random()).nextInt(10000, 100000));
                }
            }
        }

        return code;
    }
}


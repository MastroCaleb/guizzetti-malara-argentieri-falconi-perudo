package server;

import game.player.Player;
import server.lobbies.Lobby;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server implements Runnable{

    public static LinkedList<Player> clients = new LinkedList<Player>();
    public static LinkedList<Lobby> lobbies = new LinkedList<Lobby>();
    int port;

    public Server(int port){
        this.port = port;
    }

    @Override
    public void run(){
        try {
            System.out.println("Starting Server");
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server is listening...");
            String data = "";
            while(true){
                Socket client = serverSocket.accept();

                data = new DataInputStream(client.getInputStream()).readUTF();

                Player player = new Player(data, client);

                clients.add(player);
                new Thread(new HandleClient(player)).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

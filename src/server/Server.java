package server;

import game.players.client.ClientPlayer;
import game.players.server.ServerPlayer;
import server.lobbies.Lobby;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server implements Runnable{

    public static LinkedList<ServerPlayer> clients = new LinkedList<ServerPlayer>();
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

                System.out.println(data);

                ClientPlayer clientPlayer = new ClientPlayer();
                clientPlayer.fromString(data);

                System.out.println(clientPlayer.getName());

                ServerPlayer serverPlayer = new ServerPlayer(clientPlayer, client);

                clients.add(serverPlayer);
                new Thread(new HandleClient(serverPlayer)).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

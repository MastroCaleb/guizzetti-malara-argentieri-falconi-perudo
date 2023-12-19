package server;

import server.lobbies.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server implements Runnable{

    public static LinkedList<Socket> clients = new LinkedList<Socket>();
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
            System.out.println("Server in ascolto...");
            while(true){
                Socket client = serverSocket.accept();
                clients.add(client);
                new Thread(new HandleClient(client)).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

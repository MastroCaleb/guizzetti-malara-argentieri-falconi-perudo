package server;

import game.player.Player;
import server.service.HandleCommands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

public class HandleClient implements Runnable{

    Player player;

    public HandleClient(Player player){
        this.player = player;
    }

    @Override
    public void run(){
        try{

            if(player.getClient().isConnected()){
                System.out.println(player.getName() + " has connected.");
            }

            while(player.getClient().isConnected()){
                try {
                    DataInputStream inputStream = new DataInputStream(player.getClient().getInputStream());
                    String message = inputStream.readUTF();
                }
                catch (SocketException e) {
                    player.getClient().close();
                    Server.clients.remove(player);
                    break;
                }
            }

            if(player.getClient().isClosed()){
                System.out.println(player.getName() + " has disconnected.");
            }
        }
        catch(IOException e){
            System.out.println("ERRORE");
        }
    }

    void sendToAll(String message) throws IOException {
        for(Player c : Server.clients){
            if(!c.equals(player)) {
                DataOutputStream outputStream = new DataOutputStream(c.getClient().getOutputStream());
                outputStream.writeUTF(message);
            }
        }
    }

    //DEBUG

    void chatSystem(){
        try{
            new Thread(new HandleCommands(player.getClient()));

            if(player.getClient().isConnected()){
                System.out.println("A client has connected.");
            }

            while(player.getClient().isConnected()){
                try {
                    DataInputStream inputStream = new DataInputStream(player.getClient().getInputStream());
                    String message = inputStream.readUTF();
                    System.out.println(message);
                    sendToAll(message);
                }
                catch (SocketException e) {
                    player.getClient().close();
                    Server.clients.remove(player);
                    break;
                }
            }

            if(player.getClient().isClosed()){
                System.out.println("A client has disconnected.");
            }
        }
        catch(IOException e){
            System.out.println("ERRORE");
        }
    }

}

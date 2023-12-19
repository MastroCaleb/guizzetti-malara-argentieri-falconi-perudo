package server;

import server.lobbies.Lobby;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class HandleClient implements Runnable{

    Socket client;

    public HandleClient(Socket client){
        this.client = client;
    }

    @Override
    public void run(){
        try{
            if(client.isConnected()){
                System.out.println("A client has connected.");
            }

            while(client.isConnected()){
                try {
                    DataInputStream inputStream = new DataInputStream(client.getInputStream());
                    String message = inputStream.readUTF();

                    if(message.equals("LobbyList")){
                        if(Server.lobbies.isEmpty()){
                            sendToThis("No available lobbies.");
                        }
                        else{
                            String lobbyList = "";
                            int count = 0;
                            for(Lobby lobby : Server.lobbies){
                                if(lobby.isPublic()){
                                    count++;
                                    lobbyList = count + ". " + lobby.getName() + " " + lobby.playerCount() + "\n";
                                }
                            }
                            sendToThis(lobbyList);
                        }
                    }
                }
                catch (SocketException e) {
                    client.close();
                    Server.clients.remove(client);
                    break;
                }
            }

            if(client.isClosed()){
                System.out.println("A client has disconnected.");
            }
        }
        catch(IOException e){
            System.out.println("ERRORE");
        }
    }

    void sendToThis(String message) throws IOException {
        for(Socket c : Server.clients){
            if(c.equals(client)) {
                DataOutputStream outputStream = new DataOutputStream(c.getOutputStream());
                outputStream.writeUTF(message);
            }
        }
    }

    void sendToAll(String message) throws IOException {
        for(Socket c : Server.clients){
            if(!c.equals(client)) {
                DataOutputStream outputStream = new DataOutputStream(c.getOutputStream());
                outputStream.writeUTF(message);
            }
        }
    }

    //DEBUG

    void chatSystem(){
        try{
            if(client.isConnected()){
                System.out.println("A client has connected.");
            }

            while(client.isConnected()){
                try {
                    DataInputStream inputStream = new DataInputStream(client.getInputStream());
                    String message = inputStream.readUTF();
                    System.out.println(message);
                    sendToAll(message);
                }
                catch (SocketException e) {
                    client.close();
                    Server.clients.remove(client);
                    break;
                }
            }

            if(client.isClosed()){
                System.out.println("A client has disconnected.");
            }
        }
        catch(IOException e){
            System.out.println("ERRORE");
        }
    }

}

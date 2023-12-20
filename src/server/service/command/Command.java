package server.service.command;

import game.players.server.ServerPlayer;
import server.Server;
import utils.Serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class Command {
    private String command;

    public Command(String command){
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
    public void run(Socket client) throws IOException {}

    void sendToThis(Socket client, String message) throws IOException {
        for(ServerPlayer c : Server.clients){
            if(c.equals(client)) {
                DataOutputStream outputStream = new DataOutputStream(c.getClient().getOutputStream());
                outputStream.writeUTF(message);
            }
        }
    }

}

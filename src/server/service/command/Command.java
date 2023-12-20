package server.service.command;

import game.player.Player;
import server.Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        for(Player c : Server.clients){
            if(c.equals(client)) {
                DataOutputStream outputStream = new DataOutputStream(c.getClient().getOutputStream());
                outputStream.writeUTF(message);
            }
        }
    }

}

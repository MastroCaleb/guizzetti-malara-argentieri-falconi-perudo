package server.service.command;

import server.Server;
import server.lobbies.Lobby;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class Commands {

    public static LinkedList<Command> commands = new LinkedList<Command>();

    public static Command lobbyList = registerCommands(
            new Command("LobbyList"){
                @Override
                public void run(Socket client) throws IOException {

                    if(Server.lobbies.isEmpty()){
                        sendToThis(client,"No public lobbies available.");
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
                        sendToThis(client, lobbyList);
                    }
                }
            }
    );


    public static Command registerCommands(Command command){
        commands.add(command);
        return command;
    }


}

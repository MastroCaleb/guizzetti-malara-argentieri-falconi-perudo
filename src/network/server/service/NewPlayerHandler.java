package network.server.service;

import game.player.Player;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.server.Server;
import network.server.lobbies.Lobby;
import network.server.lobbies.settings.LobbySettings;

public class NewPlayerHandler implements Runnable {
    private Logger LOGGER = Logger.getLogger("NewPlayerHandler");
    private Player player;

    public NewPlayerHandler(Player player) {
        this.player = player;
    }

    public void run() {
        try {
            while(true) {

                DataInputStream inputStream = new DataInputStream(this.player.getClient().getInputStream());

                this.player.sendToThis("");
                this.player.sendToThis(Server.getLobbyList());
                this.player.sendToThis("");

                this.player.sendToThis("askCreateOrJoinLobby");

                String createOrJoin = inputStream.readUTF();

                if (createOrJoin.equals("createLobby")) {
                    this.LOGGER.log(Level.INFO, "New Lobby created.");
                    this.player.sendToThis("New Lobby created.");

                    this.player.sendToThis("askLobbySettings");

                    String settings = inputStream.readUTF();

                    String code = Server.getRandomCode();

                    LobbySettings lobbySettings = new LobbySettings(code, settings);
                    Lobby lobby = new Lobby(this.player, lobbySettings);

                    lobby.joinLobby(this.player);

                    Server.lobbies.add(lobby);
                    (new Thread(lobby)).start();
                }
                else if (createOrJoin.contains("joinLobby:")){

                    String code = createOrJoin.replace("joinLobby:", "");
                    Lobby lobby = Server.getLobbyFromCode(code);

                    if (lobby == null) {
                        System.out.println("No Lobby with this code was found.");
                    }
                    else if(!lobby.isPublic()){
                        this.player.sendToThis("askForPassword");
                        String password = inputStream.readUTF();
                        if (lobby.getSettings().getPassword().equals(password)) {
                            this.player.sendToThis("Correct password.");
                            lobby.joinLobby(this.player);
                        }
                        else{
                            this.player.sendToThis("Wrong password.");
                        }
                    }
                    else {
                        lobby.joinLobby(this.player);
                    }
                }
            }
        }
        catch (IOException var7) {
            this.LOGGER.log(Level.SEVERE, "IO Error");
        }
    }
}


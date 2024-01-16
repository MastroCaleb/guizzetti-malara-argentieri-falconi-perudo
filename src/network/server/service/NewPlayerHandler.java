package network.server.service;

import network.game.player.Player;
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

    @Override
    public void run() {
        try {
            while(true) {
                DataInputStream inputStream = new DataInputStream(this.player.getClient().getInputStream());

                while(this.player.getName().isEmpty()) {
                    player.sendToThis("askNickName");

                    String name = inputStream.readUTF();

                    if (!Server.nickIsUsed(name)) {
                        player = new Player(name, player.getClient());
                        break;
                    }
                    else {
                        player.sendToThis("Nickname not available.");
                    }
                }

                Lobby disconnectedLobby = wasDisconnectedFromLobby(player);

                if(disconnectedLobby != null){
                    this.player.sendToThis("askToReconnect");

                    String action = inputStream.readUTF().replace("action:", "");

                    if(action.equals("1")){
                        this.player.sendToThis("Reconnecting to this lobby...");
                        disconnectedLobby.reJoinLobby(player);
                        break;
                    }
                }

                this.LOGGER.log(Level.INFO, player.getName() + " has connected to the server.");

                Server.players.add(player);

                this.player.sendToThis("");
                this.player.sendToThis(Server.getLobbyList());
                this.player.sendToThis("");

                this.player.sendToThis("askCreateOrJoinLobby");

                String createOrJoin = inputStream.readUTF();

                if (createOrJoin.equals("createLobby")) {
                    this.LOGGER.log(Level.INFO, "New Lobby created.");

                    this.player.sendToThis("askLobbySettings");

                    String settings = inputStream.readUTF();

                    String code = Server.getRandomCode();

                    LobbySettings lobbySettings = new LobbySettings();
                    lobbySettings.read("lobbyCode:"+code+";"+settings);

                    Lobby lobby = new Lobby(this.player, lobbySettings);

                    player.sendToThis("The code for your lobby is: " + code);

                    lobby.joinLobby(this.player);

                    Server.lobbies.add(lobby);
                    (new Thread(lobby)).start();
                    break;
                }
                else if (createOrJoin.contains("joinLobby:")){

                    String code = createOrJoin.replace("joinLobby:", "");
                    Lobby lobby = Server.getLobbyFromCode(code);

                    if (lobby == null) {
                        System.out.println("No Lobby with this code was found.");
                    }
                    else if(lobby.isPublic()){
                        if(!lobby.hasStarted()){
                            this.player.sendToThis("Connecting to this lobby...");
                            lobby.joinLobby(this.player);
                        }
                        else if (lobby.wasDisconnected(player)) {
                            this.player.sendToThis("Reconnecting to this lobby...");
                            lobby.reJoinLobby(this.player);
                        }

                        break;
                    }
                    else if(!lobby.isPublic()){
                        while(true) {
                            this.player.sendToThis("askForPassword");

                            String password = inputStream.readUTF();

                            if (lobby.getSettings().getPassword().equals(password)) {

                                if(!lobby.hasStarted()){
                                    this.player.sendToThis("Correct password.");
                                    this.player.sendToThis("Connecting to this lobby...");
                                    lobby.joinLobby(this.player);
                                }
                                else if (lobby.wasDisconnected(player)) {
                                    this.player.sendToThis("Correct password.");
                                    this.player.sendToThis("Reconnecting to this lobby...");
                                    lobby.reJoinLobby(this.player);
                                }

                                break;
                            }
                            else {
                                this.player.sendToThis("Wrong password.");
                            }
                        }
                        break;
                    }
                }
                else if(createOrJoin.equals("updateLobby")){
                    player.sendToThis("Updating lobby list.");
                    player.sendToThis("");
                }
            }
        }
        catch (IOException e) {
            Server.players.remove(player);
            LOGGER.log(Level.WARNING, player.getName() + " has disconnected from the server.");
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Lobby wasDisconnectedFromLobby(Player player){
        for (Lobby lobby : Server.lobbies){
            if(lobby.wasDisconnected(player)){
                return lobby;
            }
        }
        return null;
    }

}


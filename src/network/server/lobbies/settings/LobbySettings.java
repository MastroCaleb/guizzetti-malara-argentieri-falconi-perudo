package network.server.lobbies.settings;

import network.packets.Packet;

import java.lang.reflect.Field;

/**
 * Class that contains each lobby's settings.
 */
@SuppressWarnings("all")
public class LobbySettings extends Packet {
    private String lobbyCode = "";
    private boolean isPublic;
    private String password = "";
    private int maxPlayers = 6;
    private int minPlayers = 2;
    private int maxDices = 5;
    private boolean jollies;
    private boolean sockIt;

    public LobbySettings(){}

    public String getLobbyCode() {
        return this.lobbyCode;
    }
    public boolean isPublic() {
        return this.isPublic;
    }
    public String getPassword() {
        return this.password;
    }
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    public int getMinPlayers() {
        return this.minPlayers;
    }
    public int getMaxDices() {
        return this.maxDices;
    }
    public boolean useJollies() {
        return this.jollies;
    }
    public boolean canSockIt() {
        return this.sockIt;
    }

    public String toString(){
        String lobbySettings = "";
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                lobbySettings += "- " + field.getName() + ": " + field.get(this).toString() + "\n";
            }
            catch (IllegalAccessException e) {
                lobbySettings = "";
            }
        }
        return lobbySettings;
    }
}

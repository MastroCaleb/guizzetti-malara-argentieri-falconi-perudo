package network.server.lobbies.settings;

import network.packets.Packet;

import java.lang.reflect.Field;

/**
 * Class that contains each lobby's settings.
 */
@SuppressWarnings("all")
public class LobbySettings extends Packet {

    /**
     * This lobby's code. Used by the Players to join it.
     */
    private String lobbyCode = "";

    /**
     * True if the lobby is public. False if the lobby is private.
     */
    private boolean isPublic;

    /**
     * The password of this lobby. It's empty if the lobby is public.
     */
    private String password = "";

    /**
     * The max number of players that can connect to this lobby.
     */
    private int maxPlayers = 6;

    /**
     * The min number of players that are needed to start a game.
     */
    private int minPlayers = 2;

    /**
     * The number of Dices given when the game starts.
     */
    private int maxDices = 5;

    /**
     * True if dices can roll to a Jolly.
     */
    private boolean jollies;

    /**
     * True if players can call "Sock It".
     */
    private boolean sockIt;

    /**
     * The constructor of the class.
     */
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

    /**
     * @return The lobby settings in a convenient Sting format.
     */
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

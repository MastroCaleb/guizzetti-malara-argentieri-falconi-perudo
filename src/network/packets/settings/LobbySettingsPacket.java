package network.packets.settings;

import network.packets.Packet;

/**
 * Packet that contains a lobby's settings.
 */
@SuppressWarnings("all")
public class LobbySettingsPacket extends Packet {

    /**
     * True if the lobby is public. False if the lobby is private.
     */
    private final boolean isPublic;

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
     * @param isPublic True if the lobby is public. False if the lobby is private.
     * @param password The password of this lobby. It's empty if the lobby is public.
     * @param maxPlayers The max number of players that can connect to this lobby.
     * @param minPlayers The min number of players that are needed to start a game.
     * @param maxDices The number of Dices given when the game starts.
     * @param jollies True if dices can roll to a Jolly.
     * @param sockIt True if players can call "Sock It".
     */
    public LobbySettingsPacket(boolean isPublic, String password, int maxPlayers, int minPlayers, int maxDices, boolean jollies, boolean sockIt) {
        this.isPublic = isPublic;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.maxDices = maxDices;
        this.jollies = jollies;
        this.sockIt = sockIt;
    }

    /**
     * @return True if the lobby is public. False if the lobby is private
     */
    public boolean isPublic() {
        return this.isPublic;
    }
}

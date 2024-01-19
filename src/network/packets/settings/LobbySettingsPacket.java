package network.packets.settings;

import network.packets.Packet;

/**
 * Packet that contains a lobby's settings.
 */
@SuppressWarnings("all")
public class LobbySettingsPacket extends Packet {
    private final boolean isPublic;
    private String password = "";
    private int maxPlayers = 6;
    private int minPlayers = 2;
    private int maxDices = 5;
    private boolean jollies;
    private boolean sockIt;

    public LobbySettingsPacket(boolean isPublic, String password, int maxPlayers, int minPlayers, int maxDices, boolean jollies, boolean sockIt) {
        this.isPublic = isPublic;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.maxDices = maxDices;
        this.jollies = jollies;
        this.sockIt = sockIt;
    }

    public boolean isPublic() {
        return this.isPublic;
    }
}

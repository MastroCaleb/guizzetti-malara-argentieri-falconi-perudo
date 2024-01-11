package network.packets.settings;

import network.packets.Packet;

public class LobbySettingsPacket extends Packet {
    private boolean isPublic = true;
    private String password = "";
    private int maxPlayers = 6;
    private int minPlayers = 2;
    private int maxDices = 5;
    private boolean jollies = false;

    public LobbySettingsPacket() {
    }

    public LobbySettingsPacket(boolean isPublic, String password, int maxPlayers, int minPlayers, int maxDices, boolean jollies) {
        this.isPublic = isPublic;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.maxDices = maxDices;
        this.jollies = jollies;
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
}

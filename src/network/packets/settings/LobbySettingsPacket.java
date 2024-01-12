package network.packets.settings;

import network.packets.Packet;

public class LobbySettingsPacket extends Packet {
    private boolean isPublic;
    private String password = "";
    private int maxPlayers = 6;
    private int maxDices = 5;
    private boolean jollies;

    public LobbySettingsPacket() {
    }

    public LobbySettingsPacket(boolean isPublic, String password, int maxPlayers, int maxDices, boolean jollies) {
        this.isPublic = isPublic;
        this.password = password;
        this.maxPlayers = maxPlayers;
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

    public int getMaxDices() {
        return this.maxDices;
    }

    public boolean useJollies() {
        return this.jollies;
    }
}

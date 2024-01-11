package network.server.lobbies.settings;

import network.packets.settings.LobbySettingsPacket;
import utils.Serializer;
import utils.interfaces.Serialized;

public class LobbySettings implements Serialized {
    private String lobbyCode = "";
    private boolean isPublic = true;
    private String password = "";
    private int maxPlayers = 6;
    private int minPlayers = 2;
    private int maxDices = 5;
    private boolean jollies = false;

    public LobbySettings(String lobbyCode, String settings) {
        try {
            this.packetToObject(lobbyCode, settings);
        }
        catch (IllegalAccessException | NoSuchFieldException var4) {
            throw new RuntimeException(var4);
        }
    }

    public LobbySettings(String lobbyCode, boolean isPublic, String password, int maxPlayers, int minPlayers, int maxDices, boolean jollies) {
        this.lobbyCode = lobbyCode;
        this.isPublic = isPublic;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.maxDices = maxDices;
        this.jollies = jollies;
    }

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

    public void packetToObject(String lobbyCode, String settings) throws NoSuchFieldException, IllegalAccessException {
        LobbySettingsPacket packet = new LobbySettingsPacket();
        packet.read(settings);
        LobbySettings lobbySettings = new LobbySettings(lobbyCode, packet.isPublic(), packet.getPassword(), packet.getMaxPlayers(), packet.getMinPlayers(), packet.getMaxDices(), packet.useJollies());
        this.read(lobbySettings.toString());
    }

    public String toString() {
        return Serializer.serializeObject(this, this.getClass().getDeclaredFields());
    }

    public void read(String value) throws NoSuchFieldException, IllegalAccessException {
        Serializer.deserializeObject(this, value);
    }
}

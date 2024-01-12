package network.server.lobbies.settings;

import network.packets.Packet;
import network.packets.settings.LobbySettingsPacket;
import utils.Serializer;
import utils.interfaces.Serialized;

public class LobbySettings extends Packet {
    private String lobbyCode = "";
    private boolean isPublic;
    private String password = "";
    private int maxPlayers = 6;
    private int minPlayers = 2;
    private int maxDices = 5;
    private boolean jollies;

    public LobbySettings(){}

    /*

    These dont work.

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
     */

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

    /*
    public void packetToObject(String lobbyCode, String settings) throws NoSuchFieldException, IllegalAccessException {
        LobbySettingsPacket packet = new LobbySettingsPacket();
        packet.read(settings);

        System.out.println(settings);

        LobbySettings lobbySettings = new LobbySettings(lobbyCode, packet.isPublic(), packet.getPassword(), packet.getMaxPlayers(), packet.getMinPlayers(), packet.getMaxDices(), packet.useJollies());

        System.out.println(packet.getPassword());

        this.lobbyCode = lobbyCode;
        this.isPublic = lobbySettings.isPublic();
        this.password = lobbySettings.getPassword();
        this.maxPlayers = lobbySettings.getMaxPlayers();
        this.minPlayers = lobbySettings.getMinPlayers();
        this.maxDices = lobbySettings.getMaxDices();
        this.jollies = lobbySettings.useJollies();

        System.out.println(this.write());
    }
     */

}

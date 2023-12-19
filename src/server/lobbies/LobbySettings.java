package server.lobbies;

import utils.Serializer;
import utils.interfaces.Serialized;

import java.io.Serial;

public class LobbySettings implements Serialized{
    private String lobbyName;
    private boolean isPublic = true;
    private int maxPlayers = 6;
    private int maxDices = 5;

    public LobbySettings(String lobbyName, boolean isPublic, int maxPlayers, int maxDices){
        this.lobbyName = lobbyName;
        this.isPublic = isPublic;
        this.maxPlayers = maxPlayers;
        this.maxDices = maxDices;
    }

    public String getLobbyName() {
        return lobbyName;
    }
    public boolean isPublic() {
        return isPublic;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
    public int getMaxDices() {
        return maxDices;
    }

    @Override
    public String toString(){
        return Serializer.serializeObject(this, this.getClass().getFields());
    }
    @Override
    public void fromString(String value) throws NoSuchFieldException, IllegalAccessException {
        Serializer.deserializeObject(this, value);
    }
}

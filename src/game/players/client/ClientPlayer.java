package game.players.client;

import utils.Serializer;
import utils.interfaces.Serialized;

import java.net.Socket;

public class ClientPlayer implements Serialized {
    private String name;
    private Socket client;

    public ClientPlayer(Socket client){
        this.client = client;
    }

    public String getName() {
        return name;
    }
    public Socket getClient() {
        return client;
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

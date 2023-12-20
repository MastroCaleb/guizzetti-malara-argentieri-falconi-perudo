package game.players.client;

import utils.Serializer;
import utils.interfaces.Serialized;

import java.net.Socket;

public class ClientPlayer implements Serialized {
    private String name;

    public ClientPlayer(String name){
        this.name = name;
    }
    public ClientPlayer(){
    }

    public String getName() {
        return name;
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

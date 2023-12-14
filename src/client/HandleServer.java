package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class HandleServer implements Runnable{

    Socket client;

    public HandleServer(Socket client){
        this.client = client;
    }

    @Override
    public void run(){
        while(client.isConnected()){
            try {
                DataInputStream inputStream = new DataInputStream(client.getInputStream());
                System.out.println(inputStream.readUTF());
            }
            catch (IOException e){
                System.out.println("ERRORE");
            }
        }
    }
}

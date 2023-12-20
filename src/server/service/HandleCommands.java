package server.service;

import server.service.command.Command;
import server.service.command.Commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HandleCommands implements Runnable{

    private Socket client;

    public HandleCommands(Socket client){
        this.client = client;
    }

    @Override
    public void run(){
        while(client.isConnected()){
            for(Command c : Commands.commands){
                try {
                    String command = new DataInputStream(client.getInputStream()).readUTF();

                    if(c.getCommand().equals(command)){
                        c.run(client);
                    }
                }
                catch (IOException e) {
                    System.out.println("ERROR");
                }
            }
        }
    }

}

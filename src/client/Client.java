package client;

import server.lobbies.LobbySettings;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    String ip;
    int port;

    public Client(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run(){
        try{
            System.out.println("Starting Client");
            Socket socket = new Socket(ip, port);
            System.out.println("Connected to port: " + socket.getPort());

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            Scanner scan = new Scanner(System.in);

            System.out.println("Add Your Username: ");
            String name = scan.nextLine();

            output.writeUTF(name);

            new Thread(new HandleServer(socket)).start();

            while(true){
                System.out.println("Game Menu");
                System.out.println();
                System.out.println("1. Create Lobby");
                System.out.println("2. Join Lobby by Name");
                System.out.println("3. Exit");
                System.out.println();

                String choice = scan.nextLine();

                if(choice.equals("1")){


                }
                else if(choice.equals("2")){
                    output.writeUTF("LobbyList"); //Ask for list of available lobbies
                    //Receives list

                    System.out.println();

                    System.out.println("Write Lobby Name: ");
                    String lobbyName = scan.nextLine();
                }
                else if(choice.equals("3")){
                    break;
                }

            }

            /*

            while(true){
                sendData = scan.nextLine();
                output.writeUTF(name + ": " + sendData);

                System.out.println("You: " + sendData);
            }
             */
        }
        catch(IOException e){
            System.out.println("ERRORE");
        }
    }
}

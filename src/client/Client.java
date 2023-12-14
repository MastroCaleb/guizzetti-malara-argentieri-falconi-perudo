package client;

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
            System.out.println("Connesso su porta: " + socket.getPort());

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String sendData;
            Scanner scan = new Scanner(System.in);

            System.out.println("Your Nickname: ");
            String name = scan.nextLine();

            new Thread(new HandleServer(socket)).start();

            while(true){
                sendData = scan.nextLine();
                output.writeUTF(name + ": " + sendData);

                System.out.println("You: " + sendData);
            }
        }
        catch(IOException e){
            System.out.println("ERRORE");
        }
    }
}

package network.server.commands;

import java.io.IOException;
import java.net.Socket;

public class Command {
    private String command;

    public Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

    public void run(Socket client) throws IOException {
    }
}

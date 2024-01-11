package network.server.commands;

import java.util.LinkedList;

public class Commands {
    public static LinkedList<Command> commands = new LinkedList();

    public Commands() {
    }

    public static Command registerCommands(Command command) {
        commands.add(command);
        return command;
    }
}

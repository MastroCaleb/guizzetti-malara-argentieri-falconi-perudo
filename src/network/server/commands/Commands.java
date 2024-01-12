package network.server.commands;

import java.util.LinkedList;

@Deprecated
public class Commands {
    public static LinkedList<Command> commands = new LinkedList();

    //REGISTER HERE


    public static Command registerCommands(Command command) {
        commands.add(command);
        return command;
    }
}

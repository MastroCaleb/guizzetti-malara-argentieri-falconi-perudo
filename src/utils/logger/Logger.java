package utils.logger;

/**
 * Custom Logger.
 */
public class Logger {

    /**
     * This logger's ID.
     */
    private final String id;

    /**
     * The constructor of the class.
     */
    public Logger(String id){
        this.id = id;
    }

    /**
     * Log a debug message.
     * <p>
     * INFO - For useful information.
     * WARNING - A situation that could cause problems.
     * ERROR - An error that will result in consequences.
     */
    public void log(LoggerLevel level, String message){
        System.out.println("[" + level.name() + "/" + id + "]: " + message);
    }
}

package utils.logger;

/**
 * Custom Logger.
 */
public class Logger {
    private final String id;

    public Logger(String id){
        this.id = id;
    }

    public void log(LoggerLevel level, String message){
        System.out.println("[" + level.name() + "/" + id + "]: " + message);
    }
}

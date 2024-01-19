package network.server.service;

import network.game.manager.GameManager;
import network.game.player.Player;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

/**
 * Manages if a player wants to call sock it or not.
 */
public class SockItHandler implements Runnable{

    private final Logger LOGGER;

    /**
     * The instance of the player.
     */
    private final Player player;

    /**
     * The game the player is in.
     */
    private final GameManager gameManager;

    /**
     * The constructor of the class.
     * @param player The instance of the player.
     * @param gameManager The game the player is in.
     */
    public SockItHandler(Player player, GameManager gameManager){
        this.player = player;
        this.gameManager = gameManager;
        this.LOGGER = new Logger("SockItHandler(" + player.getName() + ")");
    }

    /**
     * Manages if this player wants to declare "Sock It".
     */
    @Override
    public void run(){
        try{
            //Send the packet and manage the response.
            while(!player.getClient().isClosed()){
                this.player.ask("ForSockIt");

                String interaction = player.getPlayerInteraction();

                if(interaction != null){
                    if(interaction.contains("sockIt:")){

                        String data = interaction.replace("sockIt:", "");

                        if(data.equals("Y")){
                            this.player.sendToThis("You called sock it.");
                            this.gameManager.setSockIt(player);
                        }
                        else{
                            this.player.sendToThis("You didn't call sock it.");
                        }

                        return;
                    }
                }
            }
        }
        catch (Exception e){
            //Player disconnected, leave this lobby.
            LOGGER.log(LoggerLevel.ERROR, "An Exception was found. Disconnecting " + player.getName() + " from server.");
            this.gameManager.getLobby().leaveLobby(player);
        }
    }
}

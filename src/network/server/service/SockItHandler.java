package network.server.service;

import network.game.manager.GameManager;
import network.game.player.Player;
import utils.logger.Logger;
import utils.logger.LoggerLevel;

public class SockItHandler implements Runnable{

    private final Logger LOGGER;
    private final Player player;
    private final GameManager gameManager;

    public SockItHandler(Player player, GameManager gameManager){
        this.player = player;
        this.gameManager = gameManager;
        this.LOGGER = new Logger("SockItHandler(" + player.getName() + ")");
    }
    @Override
    public void run(){
        try{
            this.player.sendToThis("askForSockIt");
            while(!player.getClient().isClosed()){

                String interaction = player.getPlayerInteraction();

                if(interaction != null){
                    if(interaction.contains("sockIt:")){

                        String data = interaction.replace("sockIt:", "");

                        if(data.equals("Y")){
                            this.player.sendToThis("You called sock it.");
                            this.gameManager.setSockIt(player);
                            return;
                        }
                        else{
                            this.player.sendToThis("You didn't call sock it.");
                            return;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            LOGGER.log(LoggerLevel.ERROR, "An Exception was found. Disconnecting " + player.getName() + " from server.");
            this.gameManager.getLobby().leaveLobby(player);
        }
    }
}

package network.server.service;

import network.game.manager.GameManager;
import network.game.player.Player;

import java.io.IOException;

public class SockItHandler implements Runnable{

    private final Player player;
    private final GameManager gameManager;

    public SockItHandler(Player player, GameManager gameManager){
        this.player = player;
        this.gameManager = gameManager;
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
        catch (IOException e){
            this.gameManager.getLobby().leaveLobby(player);
        }
    }
}

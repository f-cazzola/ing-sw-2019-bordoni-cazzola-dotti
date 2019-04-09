package it.polimi.ingsw.model;

import java.util.ArrayList;

public class Match {

//    private ArrayList<PlayerId> killShootTrack;
    private PowerUpDeck currentPowerUpDeck;
    private PowerUpDeck usedPowerUpDeck;
//    private AmmoTileDeck currentAmmoTileDeck;
//    private AmmoTileDeck usedAmmoTileDeck;
//    private WeaponDeck currentWeaponDeck;
//    private ArrayList<Player> deadPlayers;
//    private int deathsCounter;
//    private PlayerId currentPlayer;
    private ArrayList<Player> currentPlayers;
    private GameBoard board;

    public Match(PowerUpDeck currentPowerUpDeck, PowerUpDeck usedPowerUpDeck, ArrayList<Player> currentPlayers) {
        this.currentPowerUpDeck = currentPowerUpDeck;
        this.usedPowerUpDeck = usedPowerUpDeck;
        this.currentPlayers = currentPlayers;
    }

    public Player getPlayer(PlayerId id){
        for(Player tmp : currentPlayers)
            if (id.equals(tmp.getId())) {
                return tmp;
            }
        return null;
    }

    public GameBoard getBoard() {
        return board;
    }

    public void addPlayer(Player player){
        currentPlayers.add(player);
    }

    public AmmoTile drawAmmoTileCard(){
        AmmoTile tmp;
        AmmoTile emptyDeck;
        tmp = currentAmmoTileDeck.drawAmmoTile();
        if (tmp == null){
            emptyDeck = currentAmmoTileDeck;
            currentAmmoTileDeck = usedAmmoTileDeck;
            usedAmmoTileDeck = emptyDeck;
            tmp = currentAmmoTileDeck.drawAmmoTile();
        }
        return tmp;
    }

    public PowerUp drawPowerUpCard(){
        PowerUp tmp;
        PowerUpDeck emptyDeck;
        tmp = currentPowerUpDeck.drawPowerUp();
        if (tmp == null){
            emptyDeck = currentPowerUpDeck;
            currentPowerUpDeck = usedPowerUpDeck;
            usedPowerUpDeck = emptyDeck;
            tmp = currentPowerUpDeck.drawPowerUp();
        }
        return tmp;
    }
}
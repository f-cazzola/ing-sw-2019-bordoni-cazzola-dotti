package it.polimi.ingsw.model;


import org.junit.jupiter.api.Test;

public class AmmoTileDeckTest {

    @Test
    public void initializeDeck() {
        Match m = new Match();
        m.getBoard().getTurrets().forEach(TurretSquare::getAmmoTile);
    }
}
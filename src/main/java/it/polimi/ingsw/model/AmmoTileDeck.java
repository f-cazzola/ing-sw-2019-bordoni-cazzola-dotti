package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;

public class AmmoTileDeck implements Deck {
    private ArrayList<AmmoTile> ammoTiles = new ArrayList<>();

    @Override
    public void shuffle() {
        Collections.shuffle(ammoTiles);
    }

    public AmmoTile drawAmmoTile() {
        return ammoTiles.isEmpty() ? null : ammoTiles.remove(0);
    }

    public void add(AmmoTile ammoTile) {
        ammoTiles.add(ammoTile);
    }

}

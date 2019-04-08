package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Map;

public class Player {

    private PlayerId id;
    private ArrayList<PlayerId> health = new ArrayList<PlayerId>();
    private int deaths;
    private ArrayList<PlayerId> marks;
    private int points;
    private String Nickname;
//    private ArrayList<Weapon> weapons;
//    private ArrayList<PowerUp> powerUps;
    private Map<Color, Integer> ammo;
    private boolean disconnetted;
    private int availableAggregateActionCounter;
//    private PlayerState playerState;

    public PlayerId getId() {
        return id;
    }

    public void setId(PlayerId id) {
        this.id = id;
    }
}

package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.PowerUpID;

public class PowerUpView {
    private final PowerUpID type;
    private final Color color;

    public PowerUpView(PowerUpID type, Color color) {
        this.type = type;
        this.color = color;
    }
}

package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.playerstate.TargetingPlayerState;

public class SelectTargetPlayerCommand implements WeaponCommand {
    protected TargetingPlayerState currentState;
    private Player targetPlayer;

    public SelectTargetPlayerCommand(TargetingPlayerState currentState, Player targetPlayer) {
        this.currentState = currentState;
        this.targetPlayer = targetPlayer;
    }

    /**
     * This method add the target to the current state
     */
    @Override
    public void execute() {
        currentState.addTargetPlayer(targetPlayer);
    }

    /**
     * This remove the target from current state
     */
    @Override
    public void undo() {
        currentState.removeTargetPlayer(targetPlayer);
    }
}

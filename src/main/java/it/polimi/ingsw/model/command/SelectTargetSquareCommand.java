package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.Square;
import it.polimi.ingsw.model.playerstate.TargetingSquareState;

/**
 * This command represent the action of select a square as target
 */
public class SelectTargetSquareCommand implements WeaponCommand {
    private Square targetSquare;
    private TargetingSquareState currentState;

    /**
     * This constructor create a command for select a square as target
     *
     * @param currentState is the current state
     * @param targetSquare is the square selected as target
     */
    public SelectTargetSquareCommand(TargetingSquareState currentState, Square targetSquare) {
        this.currentState = currentState;
        this.targetSquare = targetSquare;
    }

    /**
     * This method add the target to the current state
     */
    @Override
    public void execute() {
        currentState.addTargetSquare(targetSquare);
    }

    /**
     * This method remove the target from the current state
     */
    @Override
    public void undo() {
        currentState.removeTargetSquare(targetSquare);
    }

    /**
     * @return true if the command is undoable
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}

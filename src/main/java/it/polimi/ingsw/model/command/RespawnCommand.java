package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.PowerUp;
import it.polimi.ingsw.model.exception.IllegalUndoException;

/**
 * This command represent the reSpawn action
 */
public class RespawnCommand implements Command {
    private Player player;
    private PowerUp powerUp;

    /**
     * This constructor create a command for reSpawn
     * @param player is the player who reSpawn
     * @param powerUp is the powerUp used for reSpawn
     */
    public RespawnCommand(Player player, PowerUp powerUp) {
        this.player = player;
        this.powerUp = powerUp;
    }

    /**
     * This method execute the command
     */
    @Override
    public void execute() {
        player.pay(powerUp);
        player.respawn(powerUp.getColor());
    }

    /**
     * This method throw an exception because after a reSpawn you can't go back
     */
    @Override
    public void undo() {
        throw new IllegalUndoException();
    }

    /**
     * @return true if the command is undoable
     */
    @Override
    public boolean isUndoable() {
        return false;
    }
}

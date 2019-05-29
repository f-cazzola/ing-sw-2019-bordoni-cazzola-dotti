package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.PowerUp;
import it.polimi.ingsw.model.exception.IllegalUndoException;
import it.polimi.ingsw.model.playerstate.IdleState;

/**
 * This command represent the action of use a tagback grenade
 */
public class UseTagbackGrenadeCommand implements Command {
    private PowerUp grenade;
    private Player player;

    /**
     * This constructor create a command for use a tagback grenade
     *
     * @param player  is the player who use the tagback grenade
     * @param grenade is the grenade
     */
    public UseTagbackGrenadeCommand(Player player, PowerUp grenade) {
        this.grenade = grenade;
        this.player = player;
    }

    /**
     * This method execute the effect of the TagbackGrenade
     */
    @Override
    public void execute() {
        player.pay(grenade);
        player.getLastShooter().addMarks(PowerUp.TAGBACK_GRENADE_MARKS, player.getId());
        player.changeState(new IdleState());
    }

    /**
     * This method throw an exception because after a TagbackGrenade you can't go back
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

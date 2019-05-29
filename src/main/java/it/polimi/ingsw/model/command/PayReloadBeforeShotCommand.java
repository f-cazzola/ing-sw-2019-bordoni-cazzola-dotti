package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.playerstate.ChoosingWeaponOptionState;
import it.polimi.ingsw.model.playerstate.PendingPaymentReloadBeforeShotState;

/**
 * This command actualize the payment for the reload before the shoot for the action possible only in the final round
 */
public class PayReloadBeforeShotCommand implements Command {
    private Player player;
    private PendingPaymentReloadBeforeShotState currentState;

    /**
     * This constructor create a command for pay the reloading
     *
     * @param player       is the player who reload
     * @param currentState is the current state
     */
    public PayReloadBeforeShotCommand(Player player, PendingPaymentReloadBeforeShotState currentState) {
        this.player = player;
        this.currentState = currentState;
    }

    /**
     * This method actualize the payment and reload the weapon
     */
    @Override
    public void execute() {
        currentState.getPendingAmmoPayment().forEach((color, amount) -> player.pay(color, amount));
        currentState.getPendingCardPayment().forEach(powerUp -> player.pay(powerUp));
        currentState.getSelectedWeapon().reload();
        player.changeState(new ChoosingWeaponOptionState(currentState.getSelectedAggregateAction(), currentState.getSelectedWeapon()));
    }

    /**
     * This method refund the player
     */
    @Override
    public void undo() {
        currentState.getPendingAmmoPayment().forEach((color, amount) -> player.refund(color, amount));
        currentState.getPendingCardPayment().forEach(powerUp -> player.refund(powerUp));
        currentState.getSelectedWeapon().unload();
        player.changeState(currentState);
    }

    /**
     * @return true if the command is undoable
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}

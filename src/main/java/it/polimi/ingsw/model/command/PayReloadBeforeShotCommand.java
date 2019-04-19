package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.playerstate.ChoosingWeaponOptionState;
import it.polimi.ingsw.model.playerstate.PendingPaymentReloadBeforeShotState;

public class PayReloadBeforeShotCommand implements Command {
    private Player player;
    private PendingPaymentReloadBeforeShotState currentState;

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
}

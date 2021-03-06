package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Weapon;
import it.polimi.ingsw.model.playerstate.*;
import it.polimi.ingsw.view.commandmessage.CommandMessage;
import it.polimi.ingsw.view.commandmessage.CommandType;
import it.polimi.ingsw.view.commandmessage.WeaponCommandMessage;

/**
 * This command represent the action of select a weapon to reload
 */
public class SelectReloadingWeaponCommand implements Command {
    /**
     * This is the player doing the command
     */
    private final Player player;
    /**
     * This is the current state of the player
     */
    private final PlayerState currentState;
    /**
     * This is the future state of the player after the command is executed
     */
    private final PlayerState nextState;
    /**
     * This is the weapon selected for the reloading
     */
    private final Weapon weapon;

    /**
     * This constructor is called from ChoosingWeaponState and set the next state to the PendingPaymentReloadBeforeShotState
     *
     * @param player       is the player doing the turn
     * @param weapon       is the weapon selected for the reloading
     * @param currentState is the current state of the player
     */
    public SelectReloadingWeaponCommand(Player player, Weapon weapon, ChoosingWeaponState currentState) {
        this.player = player;
        this.currentState = currentState;
        this.weapon = weapon;
        nextState = new PendingPaymentReloadBeforeShotState(currentState.getSelectedAggregateAction(), weapon);
    }

    /**
     * This constructor is called from ManageTurnState and set the next state to the PendingPaymentReloadWeaponState
     *
     * @param player       is the player doing the turn
     * @param weapon       is the weapon selected for the reloading
     * @param currentState is the current state of the player
     */
    public SelectReloadingWeaponCommand(Player player, Weapon weapon, ManageTurnState currentState) {
        this.player = player;
        this.currentState = currentState;
        this.weapon = weapon;
        nextState = new PendingPaymentReloadWeaponState(weapon);
    }

    /**
     * This method actualize the change of the state
     */
    @Override
    public void execute() {
        player.changeState(nextState);
    }

    /**
     * This method restore to previous state
     */
    @Override
    public void undo() {
        player.changeState(currentState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandMessage createCommandMessage() {
        return new WeaponCommandMessage(CommandType.SELECT_RELOADING_WEAPON, weapon.getName());
    }
}

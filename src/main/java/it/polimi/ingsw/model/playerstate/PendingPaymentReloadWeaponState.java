package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.PowerUp;
import it.polimi.ingsw.model.Weapon;
import it.polimi.ingsw.model.command.Command;
import it.polimi.ingsw.model.command.PayReloadWeaponCommand;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * State when the player is paying to reload
 */
public class PendingPaymentReloadWeaponState implements PendingPaymentState, PlayerState {
    /**
     * Those are the ammo selected for pay the weapon reloading
     */
    private final Map<Color, Integer> pendingAmmo;
    /**
     * This are the power up selected for paying the weapon reloading
     */
    private final List<PowerUp> pendingCardPayment;
    /**
     * This is the weapon selected for the reloading
     */
    private final Weapon selectedReloadingWeapon;

    /**
     * This constructor create the state of the player when paying for reload a weapon
     *
     * @param selectedWeapon This is the weapon selected for the reloading
     */
    public PendingPaymentReloadWeaponState(Weapon selectedWeapon) {
        pendingCardPayment = new ArrayList<>();
        pendingAmmo = new EnumMap<>(Color.class);
        this.selectedReloadingWeapon = selectedWeapon;
    }

    /**
     * This method return the selected weapon
     *
     * @return the selected weapon
     */
    public Weapon getSelectedReloadingWeapon() {
        return selectedReloadingWeapon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPendingAmmo(Color color) {
        pendingAmmo.put(color, pendingAmmo.getOrDefault(color, 0) + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPendingCard(PowerUp powerUp) {
        pendingCardPayment.add(powerUp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePendingAmmo(Color color) {
        if (pendingAmmo.getOrDefault(color, 0) <= 0)
            throw new IllegalStateException();
        pendingAmmo.put(color, pendingAmmo.get(color) - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePendingCard(PowerUp powerUp) {
        if (!pendingCardPayment.contains(powerUp))
            throw new IllegalStateException();
        pendingCardPayment.remove(powerUp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Color, Integer> getPendingAmmoPayment() {
        return pendingAmmo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PowerUp> getPendingCardPayment() {
        return pendingCardPayment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Command> getPossibleCommands(Player player) {
        List<Command> commands = new ArrayList<>();
        Map<Color, Integer> totalPending = PendingPaymentState.getTotalPendingPayment(pendingCardPayment, pendingAmmo);

        if (getSelectedReloadingWeapon().getReloadingCost().equals(totalPending)) {
            commands.add(new PayReloadWeaponCommand(player, this));
            return commands;
        }
        return PendingPaymentState.generateSelectPaymentCommand(totalPending, player, getSelectedReloadingWeapon().getReloadingCost(), this);

    }
}

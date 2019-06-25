package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.command.Command;
import it.polimi.ingsw.model.command.PayReloadBeforeShotCommand;
import it.polimi.ingsw.model.command.SelectAmmoPaymentCommand;
import it.polimi.ingsw.model.command.SelectPowerUpPaymentCommand;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PendingPaymentReloadBeforeShotState extends SelectedWeaponState implements PendingPaymentState {

    private Map<Color, Integer> pendingAmmo;
    private List<PowerUp> pendingCardPayment;

    public PendingPaymentReloadBeforeShotState(AggregateAction selectedAggregateAction, Weapon selectedWeapon) {
        super(selectedAggregateAction, selectedWeapon);
        pendingCardPayment = new ArrayList<>();
        pendingAmmo = new EnumMap<>(Color.class);
    }

    @Override
    public void addPendingAmmo(Color color) {
        pendingAmmo.put(color, pendingAmmo.getOrDefault(color, 0) + 1);
    }

    @Override
    public void addPendingCard(PowerUp powerUp) {
        pendingCardPayment.add(powerUp);
    }

    @Override
    public void removePendingAmmo(Color color) {
        if (pendingAmmo.getOrDefault(color, 0) <= 0)
            throw new IllegalStateException();
        pendingAmmo.put(color, pendingAmmo.get(color) - 1);
    }

    @Override
    public void removePendingCard(PowerUp powerUp) {
        if (!pendingCardPayment.contains(powerUp))
            throw new IllegalStateException();
        pendingCardPayment.add(powerUp);
    }

    @Override
    public Map<Color, Integer> getPendingAmmoPayment() {
        return pendingAmmo;
    }

    @Override
    public List<PowerUp> getPendingCardPayment() {
        return pendingCardPayment;
    }

    @Override
    public List<Command> getPossibleCommands(Player player) {
        List<Command> commands = new ArrayList<>();
        Map<Color, Integer> totalPending = new EnumMap<>(Color.class);
        pendingCardPayment.forEach(powerUp -> totalPending.put(powerUp.getColor(), totalPending.getOrDefault(powerUp.getColor(), 0) + 1));
        pendingAmmo.forEach((color, integer) -> totalPending.put(color, totalPending.getOrDefault(color, 0) + integer));

        if (getSelectedWeapon().getReloadingCost().equals(totalPending)){
            commands.add(new PayReloadBeforeShotCommand(player, this));
            return commands;
        }
        return PendingPaymentState.generateSelctPaymentCommand(totalPending, player, getSelectedWeapon().getReloadingCost(), this);

    }
}

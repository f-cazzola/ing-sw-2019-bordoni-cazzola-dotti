package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.command.Command;
import it.polimi.ingsw.model.command.PayReloadBeforeShotCommand;
import it.polimi.ingsw.model.command.SelectAmmoPaymentCommand;
import it.polimi.ingsw.model.command.SelectPowerUpPaymentCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingPaymentReloadBeforeShotState extends SelectedWeaponState implements PendingPaymentState {

    private Map<Color, Integer> pendingAmmo;
    private List<PowerUp> pendingCardPayment;

    public PendingPaymentReloadBeforeShotState(AggregateAction selectedAggregateAction, Weapon selectedWeapon) {
        super(selectedAggregateAction, selectedWeapon);
        pendingCardPayment = new ArrayList<>();
        pendingAmmo = new HashMap<>();
    }

    @Override
    public void addPendingAmmo(Color color) {
        pendingAmmo.put(color, pendingAmmo.getOrDefault(color, 0));
    }

    @Override
    public void addPendingCard(PowerUp powerUp) {
        pendingCardPayment.add(powerUp);
    }

    @Override
    public List<Command> getPossibleCommands(Player player) {
        List<Command> commands = new ArrayList<>();
        Map<Color, Integer> totalPending = new HashMap<>();
        pendingCardPayment.forEach(powerUp -> totalPending.put(powerUp.getColor(), totalPending.getOrDefault(powerUp.getColor(), 0) + 1 ));
        getSelectedWeapon().getReloadingCost().forEach((color, cost) -> {
            if (cost > pendingAmmo.getOrDefault(color, 0) + totalPending.getOrDefault(color, 0)) {
                if (player.getAmmo().getOrDefault(color, 0) > 0) {
                    commands.add(new SelectAmmoPaymentCommand(player, this, color));
                }
                player.getPowerUps().forEach(powerUp -> {
                    if (powerUp.getColor() == color)
                        commands.add(new SelectPowerUpPaymentCommand(player, this, powerUp));
                });
            }
        });
        if (commands.isEmpty()) {
            commands.add(new PayReloadBeforeShotCommand(player, this));
        }
        return commands;
    }
}

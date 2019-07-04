package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.PowerUp;
import it.polimi.ingsw.model.command.Command;
import it.polimi.ingsw.model.command.SelectAmmoPaymentCommand;
import it.polimi.ingsw.model.command.SelectPowerUpPaymentCommand;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * States when the player is paying to use something
 */
public interface PendingPaymentState {
    /**
     * Generates a List of {@link SelectAmmoPaymentCommand}
     *
     * @param totalPending pending ammocubes
     * @param player       player who is paying
     * @param totalCost    total cost to pay
     * @param state        player's state
     * @return List of Select Ammo Payment Commands
     */
    static List<Command> generateSelectPaymentCommand(Map<Color, Integer> totalPending, Player player, Map<Color, Integer> totalCost, PendingPaymentState state) {
        List<Command> commands = new ArrayList<>();
        totalCost.forEach((color, cost) -> {
            if (cost > totalPending.getOrDefault(color, 0)) {
                if (player.getAmmo().getOrDefault(color, 0) > 0) {
                    commands.add(new SelectAmmoPaymentCommand(player, state, color));
                }
                player.getPowerUps().forEach(powerUp -> {
                    if (powerUp.getColor() == color)
                        commands.add(new SelectPowerUpPaymentCommand(player, state, powerUp));
                });

            }
        });
        return commands;
    }

    /**
     * This method return the total value associated with the power ups and ammo passed
     *
     * @param pendingPowerUpPayment This is the list of the power up
     * @param pendingAmmo           This is the total ammo
     * @return This is total value associated with the power ups and ammo passed
     */
    static Map<Color, Integer> getTotalPendingPayment(List<PowerUp> pendingPowerUpPayment, Map<Color, Integer> pendingAmmo) {
        Map<Color, Integer> totalPending = new EnumMap<>(Color.class);
        pendingPowerUpPayment.forEach(powerUp -> totalPending.put(powerUp.getColor(), totalPending.getOrDefault(powerUp.getColor(), 0) + 1));
        pendingAmmo.forEach((color, integer) -> totalPending.put(color, totalPending.getOrDefault(color, 0) + integer));
        return totalPending;
    }

    /**
     * Adds an ammo cubes
     *
     * @param color ammocube's color
     */
    void addPendingAmmo(Color color);

    /**
     * Adds a power up
     *
     * @param powerUp power up to add
     */
    void addPendingCard(PowerUp powerUp);

    /**
     * Removes an ammo cube
     *
     * @param color ammocube's color
     */
    void removePendingAmmo(Color color);

    /**
     * Removes a power up
     *
     * @param powerUp power up to remove
     */
    void removePendingCard(PowerUp powerUp);

    /**
     * Gets pending ammocubes
     *
     * @return pending ammocubes
     */
    Map<Color, Integer> getPendingAmmoPayment();

    /**
     * Gets pending power ups
     *
     * @return pending power ups
     */
    List<PowerUp> getPendingCardPayment();
}

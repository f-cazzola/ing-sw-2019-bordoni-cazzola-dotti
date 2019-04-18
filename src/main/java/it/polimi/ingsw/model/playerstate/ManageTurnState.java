package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.command.*;

import java.util.ArrayList;
import java.util.List;

public class ManageTurnState implements PlayerState {
    @Override
    public List<Command> getPossibleCommands(Player player) {
        List<Command> commands = new ArrayList<>();
        player.getPossibleAggregateAction().forEach(aggregateAction -> commands.add(new SelectAggregateActionCommand(player, aggregateAction, this)));
        if (commands.isEmpty()){
            commands.add(new DoneCommand(player, this));
            player.getWeapons().forEach(weapon -> {
                if (!weapon.isLoaded())
                    commands.add(new SelectReloadingWeaponCommand(player, weapon, this));
            });
        }
        player.getPowerUps().forEach(powerUp -> {
            if (!powerUp.isScope() && !powerUp.isTagBackGrenade())
                commands.add(new SelectPowerUpCommand(player, powerUp, this));
        });
        return commands;
    }
}

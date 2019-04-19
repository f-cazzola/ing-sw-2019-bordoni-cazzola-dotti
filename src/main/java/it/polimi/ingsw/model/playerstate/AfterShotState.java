package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.AggregateAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Weapon;
import it.polimi.ingsw.model.command.Command;
import it.polimi.ingsw.model.command.DoneCommand;
import it.polimi.ingsw.model.command.MoveCommand;

import java.util.ArrayList;
import java.util.List;

public class AfterShotState extends SelectedWeaponState implements MovableState{
    public AfterShotState(AggregateAction selectedAggregateAction, Weapon selectedWeapon) {
        super(selectedAggregateAction, selectedWeapon);
    }

    @Override
    public List<Command> getPossibleCommands(Player player) {
        List<Command> commands = new ArrayList<>();
        if(!getSelectedWeapon().hasExtraMove())
            throw new IllegalStateException();
        player.getAccessibleSquare(getSelectedWeapon().getExtraMove()).forEach(direction -> commands.add(new MoveCommand(player, direction, this)));
        commands.add(new DoneCommand(player, this));
        return commands;
    }

    @Override
    public void useMoves() {
        getSelectedWeapon().useExtraMoves();
    }

    @Override
    public void resetMoves() {
        getSelectedWeapon().resetMoves();
    }
}

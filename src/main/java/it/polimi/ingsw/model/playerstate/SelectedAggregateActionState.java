package it.polimi.ingsw.model.playerstate;

import it.polimi.ingsw.model.AggregateAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.command.Command;
import it.polimi.ingsw.model.command.DoneCommand;
import it.polimi.ingsw.model.command.MoveCommand;
import it.polimi.ingsw.model.command.SelectShootActionCommand;

import java.util.ArrayList;
import java.util.List;

public class SelectedAggregateActionState extends AfterSelectedAggregateActionState implements MovableState{
    public SelectedAggregateActionState(AggregateAction selectedAggregateAction) {
        super(selectedAggregateAction);
    }

    @Override
    public List<Command> getPossibleCommands(Player player) {
        List<Command> commands = new ArrayList<>();
        if(!getSelectedAggregateAction().hasMoved() && getSelectedAggregateAction().getMoveNumber() > 0)
            player.getAccessibleSquare(getSelectedAggregateAction().getMoveNumber()).forEach(square -> commands.add(new MoveCommand(player, square, this)));
        if(getSelectedAggregateAction().isGrab())
            commands.addAll(player.getPosition().getGrabCommands(player, this));
        else if(getSelectedAggregateAction().isShoot())
            commands.add(new SelectShootActionCommand(player, this));
        else
            commands.add(new DoneCommand(player, this));

        return commands;
    }

    @Override
    public void useMoves() {
        getSelectedAggregateAction().useMovements();
    }

    @Override
    public void resetMoves() {
        getSelectedAggregateAction().resetMoves();
    }
}

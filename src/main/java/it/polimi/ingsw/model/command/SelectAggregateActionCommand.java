package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.AggregateAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.playerstate.ManageTurnState;
import it.polimi.ingsw.model.playerstate.SelectedAggregateActionState;

public class SelectAggregateActionCommand implements Command {
    private Player player;
    private AggregateAction aggregateAction;
    private ManageTurnState currentState;

    public SelectAggregateActionCommand(Player player, AggregateAction aggregateAction, ManageTurnState currentState) {
        this.player = player;
        this.aggregateAction = aggregateAction;
        this.currentState = currentState;
    }

    @Override
    public void execute() {
        player.changeState(new SelectedAggregateActionState(aggregateAction));
    }

    @Override
    public void undo() {
        player.changeState(currentState);
    }
}

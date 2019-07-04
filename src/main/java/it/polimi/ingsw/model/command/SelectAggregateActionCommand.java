package it.polimi.ingsw.model.command;

import it.polimi.ingsw.model.AggregateActionID;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.playerstate.ManageTurnState;
import it.polimi.ingsw.model.playerstate.SelectedAggregateActionState;
import it.polimi.ingsw.view.commandmessage.AggregateActionCommandMessage;
import it.polimi.ingsw.view.commandmessage.CommandMessage;
import it.polimi.ingsw.view.commandmessage.CommandType;

/**
 * This command represent the action of select an aggregate action
 */
public class SelectAggregateActionCommand implements Command {
    /**
     * This is the player is the player doing the command
     */
    private final Player player;
    /**
     * This is the aggregate action selected
     */
    private final AggregateActionID aggregateAction;
    /**
     * This is the current state of the player
     */
    private final ManageTurnState currentState;

    /**
     * This constructor create a command for select an aggregate action
     *
     * @param player          is the player who select the aggregate action
     * @param aggregateAction is the aggregate action selected
     * @param currentState    is the current state
     */
    public SelectAggregateActionCommand(Player player, AggregateActionID aggregateAction, ManageTurnState currentState) {
        this.player = player;
        this.aggregateAction = aggregateAction;
        this.currentState = currentState;
    }

    /**
     * This method select the aggregate action
     */
    @Override
    public void execute() {
        player.selectAggregateAction();
        player.changeState(new SelectedAggregateActionState(aggregateAction.create()));
    }

    /**
     * This method deselect the aggregate action
     */
    @Override
    public void undo() {
        player.deselectAggregateAction();
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
        return new AggregateActionCommandMessage(CommandType.SELECT_AGGREGATE_ACTION, aggregateAction);
    }
}

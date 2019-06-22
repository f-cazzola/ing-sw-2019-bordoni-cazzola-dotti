package it.polimi.ingsw.view.commandmessage;

import it.polimi.ingsw.model.AggregateActionID;

public class AggregateActionCommandMessage extends CommandMessage {
    private AggregateActionID aggregateActionID;
    private String jsonType = getClass().getSimpleName();

    public AggregateActionCommandMessage(CommandType type, AggregateActionID aggregateActionID) {
        super(type);
        this.aggregateActionID = aggregateActionID;
    }

    public AggregateActionID getAggregateActionID() {
        return aggregateActionID;
    }
}

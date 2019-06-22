package it.polimi.ingsw.view.commandmessage;

import it.polimi.ingsw.model.PlayerId;

public class PlayerCommandMessage extends CommandMessage {
    private PlayerId playerId;
    private String jsonType = getClass().getSimpleName();

    public PlayerCommandMessage(CommandType type, PlayerId playerId) {
        super(type);
        this.playerId = playerId;
    }

    public PlayerId getPlayerId() {
        return playerId;
    }
}

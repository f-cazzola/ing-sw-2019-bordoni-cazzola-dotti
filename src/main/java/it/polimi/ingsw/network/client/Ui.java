package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.PlayerId;
import it.polimi.ingsw.view.ModelView;
import it.polimi.ingsw.view.commandmessage.CommandMessage;

import java.util.List;
import java.util.Map;

public interface Ui {
    String showMessage(String toBeShown, List<String> possibleAnswers, boolean isAnswerRequired);
    void refreshView(ModelView modelView);
    int manageCommandChoice(List<CommandMessage> commands, boolean undo);
    void setViewInitializationDone();
    boolean isViewInitializationDone();
    void showLeaderBoard(Map<PlayerId, Long> leaderBoard);
    void run();
}

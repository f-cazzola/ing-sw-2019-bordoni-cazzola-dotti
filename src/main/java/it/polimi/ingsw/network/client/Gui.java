package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.ModelView;
import it.polimi.ingsw.view.gui.GuiManager;
import javafx.application.Application;
import javafx.application.Platform;

import java.util.List;

import static java.lang.Thread.sleep;

public class Gui implements Ui, Runnable {

    private String answer = "";
    private boolean ready = false;
    private boolean inputReady = false;
    private static final int TIME_TO_SLEEP = 100;

    public String showMessage(String toBeShown, List<String> possibleAnswers, boolean isAnswerRequired) {
        inputReady = false;
        System.out.println(toBeShown);//it show message even on cli
        Platform.runLater(() -> GuiManager.setMessageAndShow(toBeShown, possibleAnswers, isAnswerRequired));
        while (!inputReady) {
            try {
                sleep(TIME_TO_SLEEP);
            } catch (InterruptedException e) {
            }
        }
        GuiManager.setInputReady(false);
        System.out.println(answer);//it show answer even on cli
        return answer;
    }

    public void showGame(ModelView modelView) {
        //TODO:
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isReady() {
        return ready;
    }

    public void setGuiReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public void run() {
        Application.launch(GuiManager.class);
    }

    public void setInputReady(boolean inputReady) {
        this.inputReady = inputReady;
    }

    public void showBoard() {
        //TODO:
    }
}

package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.PlayerView;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlayerBoardController {
    @FXML
    private HBox boardWithoutAggregateAction;
    @FXML
    private VBox aggregateActionBox;
    @FXML
    private HBox playerPowerUpContainer;
    @FXML
    private HBox playerWeaponContainer;
    @FXML
    private HBox playerMarks;
    @FXML
    private HBox playerHealthBox;
    @FXML
    private HBox playerDeaths;
    @FXML
    private VBox playerAmmo;
    private Stage stage;

    public void setPlayer(PlayerView playerView) {
        String playerBoardImageURI = MainGuiController.PLAYERBOARD_IMAGES_DIR
                + playerView.getId().playerId()
                + MainGuiController.BOARD_FILE_PATTERN;
        MainGuiController.setBackgroundImageFromURI(boardWithoutAggregateAction, playerBoardImageURI);
        printPlayerBoard(playerView);
    }

    private void printPlayerBoard(PlayerView playerView) {
        MainGuiController.printPlayerBoard(playerView, aggregateActionBox, playerPowerUpContainer, playerWeaponContainer, playerMarks, playerHealthBox, playerDeaths, playerAmmo);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        stage.close();
    }

    public void update(PlayerView playerView) {
        printPlayerBoard(playerView);
    }
}

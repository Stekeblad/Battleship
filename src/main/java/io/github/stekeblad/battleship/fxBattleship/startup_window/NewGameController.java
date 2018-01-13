package io.github.stekeblad.battleship.fxBattleship.startup_window;

import io.github.stekeblad.battleship.battleship_core.GameSpecs;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * A Window that allows entering of GameSpecs values.
 * The UI layout is in NewGameWindow.fxml and links to this class
 */
public class NewGameController implements Initializable{
    // UI objects
    public TextField txt_boardY;
    public TextField txt_boardX;
    public TextField txt_carriers;
    public TextField txt_battleships;
    public TextField tet_cruisers;
    public TextField txt_submarines;
    public TextField txt_destroyers;
    public Button btn_start;
    public GridPane grid_shipNumbers;
    public AnchorPane startupWindow;

    // Variables
    private Consumer<GameSpecs> gameSpecsChosenCallback;

    // Allows the class that opened this window to get the results
    public void setGameSpecsChosenCallback(Consumer<GameSpecs> callback) {
        this.gameSpecsChosenCallback = callback;
    }

    // Executed automatically then the window is created
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Filter TextFields to only be able to contain numbers
        txt_boardX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txt_boardX.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 2) { //set max content length (2 characters)
                txt_boardX.setText(oldValue);
            }
        });
        txt_boardY.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txt_boardY.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 2) { //set max content length (2 characters)
                txt_boardY.setText(oldValue);
            }
        });
        // The fields for number of ships is inside a grid pane for nice layout and to make it easy to
        // apply the text filter to all of them I use this loop, the labels with the ship names is also in the
        // the grid pane so I match the id I given to the nodes
        for (Node node : grid_shipNumbers.getChildren()) {
            if(node.getId() != null && node.getId().startsWith("txt_")) {
                ((TextField) node).textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        ((TextField) node).setText(newValue.replaceAll("[^\\d]", ""));
                    }
                    if (newValue.length() > 1) { //set max content length (1 character)
                        ((TextField) node).setText(newValue.substring(0,1));
                    }
                });
            }
        }
    }


    public void onStartClicked(ActionEvent actionEvent) {
        // First handle empty fields or invalid input
        // max x and y are 30 because larger boards will be difficult to fit on screen and that will quickly get annoying and not fun
        // each square on the board will be 30x30 pixels, that is 900x900 pixels for a 30x30 board and a hd screen is 1080 high
        // and some margins are needed on that. Two boards beside each other is 1800, plus margins it needs to be less than 1920
        if(txt_boardX.getText().equals("")) txt_boardX.setText("10");
        if(Integer.valueOf(txt_boardX.getText()) > 30) txt_boardX.setText("30");
        if(txt_boardY.getText().equals("")) txt_boardY.setText("10");
        if(Integer.valueOf(txt_boardY.getText()) > 30) txt_boardY.setText("30");

        if(txt_battleships.getText().equals("")) txt_battleships.setText("0");
        if(txt_carriers.getText().equals("")) txt_carriers.setText("0");
        if(tet_cruisers.getText().equals("")) tet_cruisers.setText("0");
        if(txt_destroyers.getText().equals("")) txt_destroyers.setText("0");
        if(txt_submarines.getText().equals("")) txt_submarines.setText("0");

        //set gameSpecs
        gameSpecsChosenCallback.accept(new GameSpecs(
                Integer.valueOf(txt_boardX.getText()),
                Integer.valueOf(txt_boardY.getText()),
                Integer.valueOf(txt_battleships.getText()),
                Integer.valueOf(txt_carriers.getText()),
                Integer.valueOf(tet_cruisers.getText()),
                Integer.valueOf(txt_destroyers.getText()),
                Integer.valueOf(txt_submarines.getText())
        ));

        // Close
        Stage stage = (Stage) btn_start.getScene().getWindow();
        stage.close();
        actionEvent.consume();
    }
}

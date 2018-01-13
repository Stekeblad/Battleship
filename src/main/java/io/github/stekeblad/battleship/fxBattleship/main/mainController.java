package io.github.stekeblad.battleship.fxBattleship.main;

import io.github.stekeblad.battleship.battleship_core.GamePhase;
import io.github.stekeblad.battleship.battleship_core.GameSpecs;
import io.github.stekeblad.battleship.battleship_core.boards.BoardCellState;
import io.github.stekeblad.battleship.battleship_core.boards.EnemyBoard;
import io.github.stekeblad.battleship.battleship_core.boards.PlayerBoard;
import io.github.stekeblad.battleship.battleship_core.ships.*;
import io.github.stekeblad.battleship.fxBattleship.CellContentColor;
import io.github.stekeblad.battleship.fxBattleship.startup_window.NewGameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the control class for the graphical version of Battleships for two players on the same computer.
 * The UI layout is in mainWindow.fxml and links to this class
 */
public class mainController {
    // UI objects
    public Button btn_newGame;
    public AnchorPane mainWindowPane;
    public Label txt_phase;
    public Label txt_player;
    public Button btn_endTurn;
    public TextField txt_shipSize;
    public Button btn_shipOrientation;
    public Label txt_whatToDo;

    // Manually created and added UI objects
    private GridPane playerPane_1_own;
    private GridPane playerPane_2_own;
    private GridPane playerPane_1_enemy;
    private GridPane playerPane_2_enemy;

    // Variables
    private PlayerBoard playerBoard_1_own;
    private PlayerBoard playerBoard_2_own;
    private EnemyBoard playerBoard_1_enemy;
    private EnemyBoard playerBoard_2_enemy;
    private GameSpecs tempGameSpecs; // to allow accidental clicks on new game to NOT break current game
    private GameSpecs gameSpecs;
    private Stage thisStage = null;
    // Handling who and what is next
    private boolean isPlayer1;
    private GamePhase phase;
    // For ship placement
    private ArrayList<Pair<ShipType, Integer>> shipsToPlace;
    private int nextShipToPlace;

    /**
     * Called then the new game button is pressed, opens StartupWindow for entering game settings and generates all boards
     * @param actionEvent the click event created by the UI
     */
    public void onNewGameClicked(ActionEvent actionEvent) {
        if ( thisStage == null) {
            // do some initialization
            thisStage = (Stage) btn_newGame.getScene().getWindow();
            thisStage.setResizable(false);
            isPlayer1 = true;
        }

        // Create and show new game/startup window
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(mainController.class.getClassLoader().getResource("battleship_fx/NewGameWindow.fxml"));
            Parent parent = fxmlLoader.load();
            NewGameController child = fxmlLoader.getController();
            child.setGameSpecsChosenCallback(callback -> tempGameSpecs = callback);
            Scene scene = new Scene(parent, 280, 290);
            stage.setTitle("New 2-Player Game");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The code below is not executed until the window is closed because "stage.showAndWait();"
        if (tempGameSpecs == null) {
            // Do not crete a new game
            return;
        }
        //reset some things
        btn_shipOrientation.setDisable(true);
        txt_shipSize.setText("0");
        phase = GamePhase.PLACE_SHIPS;
        gameSpecs = tempGameSpecs;
        tempGameSpecs = null;
        shipsToPlace = new ArrayList<>();

        // Store all the different types of ships that should be placed in one ArrayList in a way that is easy to access
        // and still keep all the required information
        for (int i = 0; i < gameSpecs.getNumCarriers(); i++) {
            shipsToPlace.add(new Pair<>(Carrier.SHIP_TYPE, Carrier.SHIPSIZE));
        }
        for (int i = 0; i < gameSpecs.getNumBattleships(); i++) {
            shipsToPlace.add(new Pair<>(Battleship.SHIP_TYPE, Battleship.SHIPSIZE));
        }
        for (int i = 0; i < gameSpecs.getNumCruisers(); i++) {
            shipsToPlace.add(new Pair<>(Cruiser.SHIP_TYPE, Cruiser.SHIPSIZE));
        }
        for (int i = 0; i < gameSpecs.getNumSubmarines(); i++) {
            shipsToPlace.add(new Pair<>(Submarine.SHIP_TYPE, Submarine.SHIPSIZE));
        }
        for (int i = 0; i < gameSpecs.getNumDestroyers(); i++) {
            shipsToPlace.add(new Pair<>(Destroyer.SHIP_TYPE, Destroyer.SHIPSIZE));
        }
        // 0 is selected for all the different types of ship, good luck playing...
        if(shipsToPlace.size() == 0) {
            showWarning("No Ships", "Cant start the game, no ships to sink");
            return;
        }

        // Resize this window to fit the board
        thisStage.setWidth(90 + 30 * (2 * gameSpecs.getBoardSizeX()));
        thisStage.setHeight(110 + 30 * gameSpecs.getBoardSizeY());

        //Reset from possible earlier game and create playerPanes
        createPanes();

        // Create boards
        try {
            playerBoard_1_enemy = new EnemyBoard(gameSpecs.getBoardSizeX(), gameSpecs.getBoardSizeY());
            playerBoard_2_enemy = new EnemyBoard(gameSpecs.getBoardSizeX(), gameSpecs.getBoardSizeY());
            playerBoard_1_own = new PlayerBoard(gameSpecs.getBoardSizeX(), gameSpecs.getBoardSizeY());
            playerBoard_2_own = new PlayerBoard(gameSpecs.getBoardSizeX(), gameSpecs.getBoardSizeY());
        } catch (Exception e) {
            showWarning("Board size to small", e.getMessage());
            return;
        }
        fullColorAllBoards();

        // Start with player 1
        isPlayer1 = true;
        playerPane_2_enemy.setVisible(false);
        playerPane_2_own.setVisible(false);
        nextShipToPlace = 0;
        // Set label text
        txt_phase.setText("Ship placement phase");
        txt_player.setText("Player 1");
        txt_whatToDo.setText("Place your ships in the right grid      Placing ship of size:");
        txt_shipSize.setText(Integer.toString(shipsToPlace.get(0).getValue()));

        // Enable rotate ship button
        btn_shipOrientation.setDisable(false);

        actionEvent.consume();
    }

    /**
     * Updates the coloring for all cells on all boards, used then several boards have several changes
     */
    private void fullColorAllBoards() {
        BoardCellState[][] p1e = playerBoard_1_enemy.getBoard();
        BoardCellState[][] p1o = playerBoard_1_own.getBoard();
        BoardCellState[][] p2e = playerBoard_2_enemy.getBoard();
        BoardCellState[][] p2o = playerBoard_2_own.getBoard();
            for(int x = 0; x < gameSpecs.getBoardSizeX(); x++) {
                for (int y = 0; y < gameSpecs.getBoardSizeY(); y++) {
                    playerPane_1_enemy.lookup("#enemy1_" + x + "_" + y).setStyle("-fx-background-color:" + CellContentColor.getColor(p1e[x][y]));
                    playerPane_1_own.lookup("#own1_" + x + "_" + y).setStyle("-fx-background-color:" + CellContentColor.getColor(p1o[x][y]));
                    playerPane_2_enemy.lookup("#enemy2_" + x + "_" + y).setStyle("-fx-background-color:" + CellContentColor.getColor(p2e[x][y]));
                    playerPane_2_own.lookup("#own2_" + x + "_" + y).setStyle("-fx-background-color:" + CellContentColor.getColor(p2o[x][y]));
            }
        }
    }

    /**
     * Recolors a single board, used then several cells have changed on it
     * @param pane the pane to set the colors to
     * @param board the board to get the colors from
     */
    private void fullColorSingleBoard(GridPane pane, BoardCellState[][] board) {
        for(int x = 0; x < gameSpecs.getBoardSizeX(); x++) {
            for (int y = 0; y < gameSpecs.getBoardSizeY(); y++) {
                pane.lookup("#" + pane.getId() + x + "_" + y).setStyle("-fx-background-color:" + CellContentColor.getColor(board[x][y]));
            }
        }
    }

    /**
     * Colors one cell in one pane with a specific color, used when only small changes happened
     * @param pane the pane to affect
     * @param x the cell x coord inside the given pane
     * @param y the cell y coord inside the given pane
     * @param state a BoardCellState to translate to a color
     */
    private void colorSingle(GridPane pane, int x, int y, BoardCellState state) {
        pane.lookup("#" + pane.getId() + x + "_" + y).setStyle("-fx-background-color:" + CellContentColor.getColor(state));
    }

    /**
     * Locks/unlocks a entire pane from receiving click events
     * @param pane the pane to change the click lock state on
     * @param locked true if no clicks should be handle, false to allow click events
     */
    private void setPaneClickLock(GridPane pane, boolean locked) {
        for(int x = 0; x < gameSpecs.getBoardSizeX(); x++) {
            for (int y = 0; y < gameSpecs.getBoardSizeY(); y++) {
                pane.lookup("#" + pane.getId() + x + "_" + y).setDisable(locked);
            }
        }
    }

    /**
     * Creates the four gridPanes used while playing from scratch, if they already exist they are destroyed and recreated.
     * The panes are player 1 own view, player 2 own view, player 1 enemy view and player 2 enemy view.
     * <p>
     *     The panes are created, assign a Id, positioned and sized dynamically based on GameSpecs,
     *     Given buttons to be clicked and for showing ship / shot positions that all have been resized, assigned Ids and
     *     rigged to be clickable.
     * </p>
     */
    private void createPanes() {
        // If the panes already exist, destroy them
        if(playerPane_1_enemy != null) {
            mainWindowPane.getChildren().remove(playerPane_1_enemy);
            // if one pane exist, assume the others also do
            mainWindowPane.getChildren().remove(playerPane_1_own);
            mainWindowPane.getChildren().remove(playerPane_2_enemy);
            mainWindowPane.getChildren().remove(playerPane_2_own);
        }
        //create panes
        playerPane_1_enemy = new GridPane();
        playerPane_1_enemy.setLayoutX(10);
        playerPane_1_enemy.setLayoutY(40);
        playerPane_1_enemy.setId("enemy1_");
        playerPane_1_own = new GridPane();
        playerPane_1_own.setLayoutX(70 + 30 * gameSpecs.getBoardSizeX()); // anchored to the right with 60 pixels spacing to playerPane_enemy (playerPane_enemy is 10 from border)
        playerPane_1_own.setLayoutY(40);
        playerPane_1_own.setId("own1_");
        mainWindowPane.getChildren().add(playerPane_1_enemy);
        mainWindowPane.getChildren().add(playerPane_1_own);

        // They should overlap and only the panes for one player should be visible at the time.
        playerPane_2_enemy = new GridPane();
        playerPane_2_enemy.setLayoutX(10);
        playerPane_2_enemy.setLayoutY(40);
        playerPane_2_enemy.setId("enemy2_");
        playerPane_2_own = new GridPane();
        playerPane_2_own.setLayoutX(70 + 30 * gameSpecs.getBoardSizeX());
        playerPane_2_own.setLayoutY(40);
        playerPane_2_own.setId("own2_");
        mainWindowPane.getChildren().add(playerPane_2_enemy);
        mainWindowPane.getChildren().add(playerPane_2_own);

        // Make the panes the right size
        // Insert columns
        for (int x = 0; x < gameSpecs.getBoardSizeX(); x++) {
            ColumnConstraints L_constraints = new ColumnConstraints(30);
            playerPane_1_enemy.getColumnConstraints().add(L_constraints);
            ColumnConstraints R_constraints = new ColumnConstraints(30);
            playerPane_1_own.getColumnConstraints().add(R_constraints);

            ColumnConstraints L2_constraints = new ColumnConstraints(30);
            playerPane_2_enemy.getColumnConstraints().add(L2_constraints);
            ColumnConstraints R2_constraints = new ColumnConstraints(30);
            playerPane_2_own.getColumnConstraints().add(R2_constraints);
        }
        // Insert rows
        for(int y = 0; y < gameSpecs.getBoardSizeY(); y++) {
            RowConstraints L_constraints = new RowConstraints(30);
            playerPane_1_enemy.getRowConstraints().add(L_constraints);
            RowConstraints R_constraints = new RowConstraints(30);
            playerPane_1_own.getRowConstraints().add(R_constraints);

            RowConstraints L2_constraints = new RowConstraints(30);
            playerPane_2_enemy.getRowConstraints().add(L2_constraints);
            RowConstraints R2_constraints = new RowConstraints(30);
            playerPane_2_own.getRowConstraints().add(R2_constraints);
        }
        // Make a nice grid
        playerPane_1_enemy.setGridLinesVisible(true);
        playerPane_1_own.setGridLinesVisible(true);
        playerPane_2_enemy.setGridLinesVisible(true);
        playerPane_2_own.setGridLinesVisible(true);

        // Add the buttons
        for(int x = 0; x < gameSpecs.getBoardSizeX(); x++) {
            for(int y = 0; y < gameSpecs.getBoardSizeY(); y++) {
                Button enemyCell = new Button("");
                enemyCell.setId("enemy1_" + x + "_" + y);
                enemyCell.setPrefSize(30, 30);
                enemyCell.setOnMouseClicked(event -> buttonAction(enemyCell.getId()));
                enemyCell.setDisable(true);
                playerPane_1_enemy.add(enemyCell, x, y);
                Button playerCell = new Button("");
                playerCell.setId("own1_" + x + "_" + y);
                playerCell.setPrefSize(30, 30);
                playerCell.setOnMouseClicked(event -> buttonAction(playerCell.getId()));
                playerPane_1_own.add(playerCell, x, y);

                Button enemy2Cell = new Button("");
                enemy2Cell.setId("enemy2_" + x + "_" + y);
                enemy2Cell.setPrefSize(30, 30);
                enemy2Cell.setOnMouseClicked(event -> buttonAction(enemy2Cell.getId()));
                enemy2Cell.setDisable(true);
                playerPane_2_enemy.add(enemy2Cell, x, y);
                Button player2Cell = new Button("");
                player2Cell.setId("own2_" + x + "_" + y);
                player2Cell.setPrefSize(30, 30);
                player2Cell.setOnMouseClicked(event -> buttonAction(player2Cell.getId()));
                playerPane_2_own.add(player2Cell, x, y);
            }
        }
    }

    /**
     * Takes a button click and make it do things!
     * @param buttonId the id of the clicked button
     */
    private void buttonAction(String buttonId) {
        // Extract coords of the click
        String[] coords = buttonId.split("_");
        int x = Integer.valueOf(coords[1]);
        int y = Integer.valueOf(coords[2]);
        switch (phase) {
            case PLACE_SHIPS:
                // Get selected ship rotation
                ShipOrientation orientation;
                if(btn_shipOrientation.getText().equals("vertically")) {
                    orientation = ShipOrientation.VERTICAL;
                } else {
                    orientation  =ShipOrientation.HORIZONTAL;
                }
                // Figure out what type of ship to place
                BaseShip ship;
                switch (shipsToPlace.get(nextShipToPlace).getKey()) {
                    case CARRIER:
                        ship = new Carrier(x, y, orientation);
                        break;
                    case BATTLESHIP:
                        ship = new Battleship(x, y, orientation);
                        break;
                    case CRUISER:
                        ship = new Cruiser(x, y, orientation);
                        break;
                    case SUBMARINE:
                        ship = new Submarine(x, y, orientation);
                        break;
                    case DESTROYER:
                        ship = new Destroyer(x, y, orientation);
                        break;
                    default: // should never happen
                        ship = null;
                }
                // Who gets the ship
                if(isPlayer1) {
                    try {
                        playerBoard_1_own.addShip(ship);
                    } catch (Exception e) {
                        showWarning("Illegal placement", e.getMessage());
                        return;
                    }
                    fullColorSingleBoard(playerPane_1_own, playerBoard_1_own.getBoard());
                    // Get info on next ship or unlock end turn button
                    if(++nextShipToPlace == shipsToPlace.size()) { //increment counter and check if last ship just been placed
                        setPaneClickLock(playerPane_1_own, true);
                    }
                } else {
                    try {
                        playerBoard_2_own.addShip(ship);
                    } catch (Exception e) {
                        showWarning("Illegal placement", e.getMessage());
                        return;
                    }
                    fullColorSingleBoard(playerPane_2_own, playerBoard_2_own.getBoard());
                    // Get info on next ship or unlock end turn button
                    if(++nextShipToPlace == shipsToPlace.size()) { //increment counter and check if last ship just been placed
                        setPaneClickLock(playerPane_2_own, true);
                    }
                }
                // Independent of player who placed ship but requires it to be more ships to be placed
                if (nextShipToPlace == shipsToPlace.size()) {
                    btn_endTurn.setDisable(false);
                    txt_shipSize.setText("0");
                } else {
                    txt_shipSize.setText(shipsToPlace.get(nextShipToPlace).getValue().toString());
                }
                break;
            case ATTACK:
                mainWindowPane.lookup("#" + buttonId).setOnMouseClicked(null); // click has no longer any effect but looks like all other buttons?
                if(isPlayer1) {
                    // Fire the shot
                    BoardCellState cellState = playerBoard_2_own.fireAt(x, y);
                    // Update the view of the enemy board with the result
                    playerBoard_1_enemy.fireAt(x, y, cellState);
                    // Check if a single recolor is enough
                    if (cellState != BoardCellState.SUNKEN) {
                        colorSingle(playerPane_2_own, x, y, cellState);
                        colorSingle(playerPane_1_enemy, x, y, cellState);
                    } else { // sunken modifies more then one cell to show the entire hit ship as sunken
                        fullColorSingleBoard(playerPane_2_own, playerBoard_2_own.getBoard());
                        fullColorSingleBoard(playerPane_1_enemy, playerBoard_1_enemy.getBoard());
                    }
                    // Disable clicking in entire pane to prevent more shots
                    setPaneClickLock(playerPane_1_enemy, true);
                    txt_shipSize.setText(String.valueOf(playerBoard_2_own.getShipsLeft()));
                    // Check game over
                    if (playerBoard_2_own.getShipsLeft() == 0) {
                        showWarning("Game Over!", "Player 1 has destroyed all of player 2s ships!");
                        return;
                    }
                } else {
                    BoardCellState cellState = playerBoard_1_own.fireAt(x, y);
                    playerBoard_2_enemy.fireAt(x, y, cellState);
                    if (cellState != BoardCellState.SUNKEN) {
                        colorSingle(playerPane_1_own, x, y, cellState);
                        colorSingle(playerPane_2_enemy, x, y, cellState);
                    } else { // sunken modifies more then one cell to show the entire hit ship as sunken
                        fullColorSingleBoard(playerPane_1_own, playerBoard_1_own.getBoard());
                        fullColorSingleBoard(playerPane_2_enemy, playerBoard_2_enemy.getBoard());
                    }
                    // Disable clicking to prevent more shots
                    setPaneClickLock(playerPane_2_enemy, true);
                    txt_shipSize.setText(String.valueOf(playerBoard_1_own.getShipsLeft()));
                    // Check game over
                    if (playerBoard_1_own.getShipsLeft() == 0) {
                        showWarning("Game Over!", "Player 2 has destroyed all of player 1s ships!");
                    }
                }
                // End turn
                btn_endTurn.setDisable(false);
                break;
            default:
                System.err.println("Unhandled phase");
        }
    }

    /**
     * Ends a player's turn. Changes the grid panes visibility
     * @param actionEvent the click event created by the UI
     */
    public void onEndTurnClicked(ActionEvent actionEvent) {
        // Who is ending their turn?
        if (isPlayer1) {
            playerPane_1_enemy.setVisible(false);
            playerPane_1_own.setVisible(false);

            // Tell players to swap and wait for confirmation
            showWarning("Swap", "Player 2, its your turn now!");
            playerPane_2_enemy.setVisible(true);
            playerPane_2_own.setVisible(true);
            txt_player.setText("Player 2");
        } else {
            playerPane_2_enemy.setVisible(false);
            playerPane_2_own.setVisible(false);

            // Tell players to swap and wait for confirmation
            showWarning("Swap", "Player 1, its your turn now!");
            playerPane_1_enemy.setVisible(true);
            playerPane_1_own.setVisible(true);
            txt_player.setText("Player 1");
        }
        // Phase specific actions
        if (phase == GamePhase.PLACE_SHIPS) {
            if(isPlayer1) {
                nextShipToPlace = 0;
                txt_shipSize.setText(shipsToPlace.get(0).getValue().toString());
                playerBoard_1_own.removePlacementGuide();
                fullColorSingleBoard(playerPane_1_own, playerBoard_1_own.getBoard());
            } else {
                phase = GamePhase.ATTACK;
                txt_phase.setText("Attack Phase");
                btn_shipOrientation.setDisable(true);
                playerBoard_2_own.removePlacementGuide();
                fullColorSingleBoard(playerPane_2_own, playerBoard_2_own.getBoard());
                shipsToPlace = null; //not needed, free it
                setPaneClickLock(playerPane_1_enemy, false); // allow player 1 to start attacking
                txt_whatToDo.setText("Fire at your opponent in the left pane.      Enemy ships left:");
                txt_shipSize.setText(String.valueOf(playerBoard_2_own.getShipsLeft()));
            }
        } else { // attack phase, unlock enemy pane (in PLACE_SHIP the own board was created unlocked and locks after placement finished
            if(isPlayer1) {
                setPaneClickLock(playerPane_2_enemy, false);
                txt_shipSize.setText(String.valueOf(playerBoard_1_own.getShipsLeft()));
            } else {
                setPaneClickLock(playerPane_1_enemy, false);
                txt_shipSize.setText(String.valueOf(playerBoard_2_own.getShipsLeft()));
            }
        }
        // now is it the other player's turn
        isPlayer1 = !isPlayer1;
        // Disable end turn button and done
        btn_endTurn.setDisable(true);
        actionEvent.consume();
    }

    /**
     * Allows the player to control witch orientation the ship will be placed in, click just toggles text and the
     * text is used to decide ship orientation on placement
     * @param actionEvent the click event created by the UI
     */
    public void onShipOrientationClicked(ActionEvent actionEvent) {
        if(btn_shipOrientation.getText().equals("vertically")) {
            btn_shipOrientation.setText("horizontally");
        } else {
            btn_shipOrientation.setText("vertically");
        }
        actionEvent.consume();
    }

    /**
     * A convenient method for showing a warning dialog
     * @param title text to be displayed in window title bar
     * @param desc the message to tell the player
     */
    private void showWarning(String title, String desc) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setContentText(desc);
        alert.showAndWait();
    }
}

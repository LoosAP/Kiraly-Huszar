package game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.model.GameModel;
import game.model.SquareStates;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import static game.model.GameModel.SIZE;
import static game.model.SquareStates.*;

public class GameController {


    @FXML
    private GridPane board;

    private GameModel model = new GameModel();

    /**
     * Stores the moves made by the user
     */
    ArrayList<MoveTracker> undoTracker = new ArrayList<>();

    /**
     * Stores the moves undone by the user
     */
    ArrayList<MoveTracker> redoTracker = new ArrayList<>();

    @FXML
    private void initialize(){
        for (var i = 0; i < board.getRowCount(); i++) {
            for (var j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare(i, j);
                board.add(square, j, i);
            }
        }
    }

    /**
     * Creates a square with the specified row and column
     * @param i the row
     * @param j the column
     * @return the square
     */
    private StackPane createSquare(int i, int j) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        if ( (i+j) % 2 == 1 ){
            square.setStyle("-fx-background-color: #9DC08B");
        }
        else {
            square.setStyle("-fx-background-color: #EDF1D6");
        }
        var piece = new ImageView();

        piece.imageProperty().bind(
                new ObjectBinding<Image>() {
                    {
                        super.bind(model.squareProperty(i, j));
                    }
                    @Override
                    protected Image computeValue() {
                        return switch (model.squareProperty(i, j).get()) {
                            case KNIGHT -> new Image(getClass().getResourceAsStream("/knight.png"));
                            case KING -> new Image(getClass().getResourceAsStream("/king.png"));
                            case GOAL -> new Image(getClass().getResourceAsStream("/goal.png"));
                            case NONE -> null;
                        };
                    }
                }
        );
        square.getChildren().add(piece);
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private SquareStates selectedPiece = null;

    /**
     * Handles the mouse click event
     * @param event the mouse click event
     */
    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);

        //check if the selected square is within valid ranges
        if (row < 0 || row > SIZE || col < 0 || col > SIZE) {
            return;
        }
        // the state of the newly clicked square
        SquareStates newSelectedState = model.getSquareState(row,col);
        // if the clicked square is empty and a chess piece is selected
        if (newSelectedState == NONE && selectedPiece != null){
            if (model.canMovePiece(row,col,selectedPiece)){
                undoTracker.add(new MoveTracker(row,col,selectedPiece,model.getRow(selectedPiece),model.getCol(selectedPiece)));
                redoTracker.clear();
                model.move(row,col,selectedPiece);

                selectedPiece = null;
            }
            else {
                System.out.printf("%s cannot move to (%d,%d)%n",selectedPiece,row,col);
            }
        }
        // if successfully moved into goal
        else if (newSelectedState == GOAL && selectedPiece != null){
            if (model.canMovePiece(row,col,selectedPiece)){
                model.move(row,col,selectedPiece);
                System.out.printf("%s successfully moved into goal%n",selectedPiece);
                selectedPiece = null;
                winGame();
            }
            else {
                System.out.printf("%s cannot move to (%d,%d)%n",selectedPiece,row,col);
            }

        }

        // if the selectedPiece is null and clicked on a square occupied by a valid chess piece
        else if (selectedPiece == null && newSelectedState != NONE && newSelectedState != GOAL){
            if (model.isInCheck(newSelectedState)){
                selectedPiece = newSelectedState;
                System.out.printf("Selected %s on square (%d,%d)%n",selectedPiece,row,col);
            }
            else {
                System.out.printf("%s is not in check%n",newSelectedState);
            }



        }
        // if clicked on the same piece as before
        else if (selectedPiece == newSelectedState){
            System.out.printf("Deselected %s %n",selectedPiece);
            selectedPiece = null;
        }

    }

    /**
     * Clears the board and sets the pieces to a new random position
     */
    public void onNewGame(ActionEvent actionEvent) {
        // reset variables
        selectedPiece = null;
        undoTracker.clear();
        redoTracker.clear();
        model.clearBoard();

        // Generating random numbers
        Random random = new Random();
        int kingX = random.nextInt(8);
        int kingY = random.nextInt(8);
        int knightX = random.nextInt(8);
        int knightY = random.nextInt(8);
        int goalX = random.nextInt(8);
        int goalY = random.nextInt(8);

        model.setKing(kingX,kingY);
        model.setKnight(knightX,knightY);

        // Checks if the random numbers are the same and at least one piece is in check
        while (!model.isInCheck(KING) && !model.isInCheck(KNIGHT) || kingX == knightX || kingY == knightY){

                knightX = random.nextInt(8);
                knightY = random.nextInt(8);
                kingX = random.nextInt(8);
                kingY = random.nextInt(8);
                model.setKing(kingX,kingY);
                model.setKnight(knightX,knightY);


        }
        model.setBoard(kingX,kingY, KING);
        model.setBoard(knightX,knightY, KNIGHT);

        while ((goalX == kingX && goalY == kingY) || (goalX == knightX && goalY == knightY)){
            goalX = random.nextInt(8);
            goalY = random.nextInt(8);
        }

        model.setBoard(goalX,goalY, GOAL);
        model.setGoal(goalX,goalY);
    }

    /**
     * Saves the current state of the board, the undoTracker and the redoTracker using jackson
     */
    public void onSave(ActionEvent actionEvent) throws JsonProcessingException {
        // stores the current state of the board, the undoTracker and the redoTracker using jackson
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Object> save = new ArrayList<>();
        save.add(model.getPositions());
        save.add(objectMapper.writeValueAsString(undoTracker));
        save.add(objectMapper.writeValueAsString(redoTracker));
        try {
            objectMapper.writeValue(new File("save.json"),save);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads the save.json file and sets the board to the saved state, and sets the undoTracker and redoTracker to the saved state
     */
    public void onLoad(ActionEvent actionEvent) throws JsonProcessingException {
        // loads save.json sets the board to the saved state, and sets the undoTracker and redoTracker to the saved state
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Object> load = new ArrayList<>();
        try {
            load = objectMapper.readValue(new File("save.json"), new TypeReference<ArrayList<Object>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.setPositions((ArrayList<Integer>) load.get(0));
        undoTracker.clear();
        redoTracker.clear();
        undoTracker= objectMapper.readValue((String) load.get(1), new TypeReference<ArrayList<MoveTracker>>() {});
        redoTracker= objectMapper.readValue((String) load.get(2), new TypeReference<ArrayList<MoveTracker>>() {});


    }

    /**
     * A popup window that asks the user if they want to exit the game
     */
    public void onExit(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("All unsaved progress will be lost");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Platform.exit();
        }
    }

    /**
     * Undoes the last move
     */
    public void onUndo(ActionEvent actionEvent) {
        try{
            //gets the last element of the moveTracker arraylist
            MoveTracker lastMove = undoTracker.get(undoTracker.size()-1);
            //moves the piece back to its previous position
            model.move(lastMove.getPrevRow(),lastMove.getPrevCol(),lastMove.getPrevPiece());
            // adds the last move to redoTracker
            redoTracker.add(lastMove);
            //removes the last element of the arraylist
            undoTracker.remove(undoTracker.size()-1);
        }
        catch (Exception e){
            System.out.println("No more moves to undo");
        }


    }

    /**
     * Redoes the last move
     */
    public void onRedo(ActionEvent actionEvent) {
        try{
            //gets the last element of the redoTracker arraylist
            MoveTracker lastMove = redoTracker.get(redoTracker.size()-1);
            //moves the piece back to its previous position
            model.move(lastMove.getCurrentRow(),lastMove.getCurrentCol(),lastMove.getPrevPiece());
            // adds the last move to undoTracker
            undoTracker.add(lastMove);
            //removes the last element of the arraylist
            redoTracker.remove(redoTracker.size()-1);
        }
        catch (Exception e){
            System.out.println("No more moves to redo");
        }
    }

    /**
     * Opens a link to the github repository
     */
    public void onAbout(ActionEvent actionEvent) {
        try {
            URI uri = URI.create("https://github.com/INBPM0420L/homework-project-LoosAP");
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If the user has won the game, then an alert window will pop up asking if the user wants to play again
     * If the user clicks yes, then the game will reset and start a new game
     * If the user clicks no, then the game closes
     */
    public void winGame(){
        // An alert window that shows the user has won the game, and asks if they want to play again
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("You have won the game!");
        alert.setContentText("Would you like to play again?");

        // if the user clicks yes, then the game will reset and start a new game
        Optional<ButtonType> result = alert.showAndWait();
        if (((Optional<?>) result).get() == ButtonType.OK){
            onNewGame(null);
        }
        // if the user clicks no, then the game closes
        else {
            System.exit(0);
        }
    }
}

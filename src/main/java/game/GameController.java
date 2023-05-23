package game;

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

    // tracks the moves made by the player
    ArrayList<MoveTracker> undoTracker = new ArrayList<>();

    // tracks the moves made by undoTracker
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

    public void onSave(ActionEvent actionEvent) {
    }

    public void onLoad(ActionEvent actionEvent) {
    }

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

    public void onHints(ActionEvent actionEvent) {
    }

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

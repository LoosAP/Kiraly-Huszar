package game;

import game.model.GameModel;
import game.model.SquareStates;
import javafx.beans.binding.ObjectBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.Random;

import static game.model.GameModel.SIZE;
import static game.model.SquareStates.*;

public class GameController {

    @FXML
    private GridPane board;

    private GameModel model = new GameModel();

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
        if (row <= 0 || row > SIZE || col <= 0 || col > SIZE) {
            return;
        }
        // the state of the newly clicked square
        SquareStates newSelectedState = model.getSquareState(row,col);
        // if the clicked square is empty and a chess piece is selected
        if (newSelectedState == NONE && selectedPiece != null){
            if (canMovePiece(selectedPiece)){
                model.move(row,col,selectedPiece);

                selectedPiece = null;
            }
        }
        // TODO: if successfully moved into goal

        //if the selectedPiece is null and clicked on a square occupied by a valid chess piece
        else if (selectedPiece == null && newSelectedState != NONE && newSelectedState != GOAL){
            selectedPiece = newSelectedState;

            System.out.printf("Selected %s on square (%d,%d)%n",selectedPiece,row,col);
        }
    }

    //checks if the move we want to perform is valid
    private boolean canMovePiece(SquareStates selectedPiece) {
        switch (selectedPiece){
            case KING -> {}
            case KNIGHT -> {}
        }
        return true;
    }

    public void onNewGame(ActionEvent actionEvent) {
        for (var i = 0; i < board.getRowCount(); i++) {
            for (var j = 0; j < board.getColumnCount(); j++) {

                model.setBoard(i,j,NONE);
            }
        }
        Random random = new Random();
        int boardX = random.nextInt(8);
        int boardY = random.nextInt(8);
        int goalX = random.nextInt(8);
        int goalY = random.nextInt(8);
        if (boardX == 7){
            model.setBoard(boardX,boardY,KNIGHT);
            model.setBoard(boardX-1,boardY,KING);
            while ((goalX == boardX && goalY == boardY) || (goalX == boardX-1 && goalY == boardY)){
                goalX = random.nextInt(8);
                goalY = random.nextInt(8);
            }
            model.setBoard(goalX,goalY,GOAL);
        }
        else {
            model.setBoard(boardX,boardY,KNIGHT);
            model.setBoard(boardX+1,boardY,KING);
            while ((goalX == boardX && goalY == boardY) || (goalX == boardX+1 && goalY == boardY)){
                goalX = random.nextInt(8);
                goalY = random.nextInt(8);
            }
            model.setBoard(goalX,goalY,GOAL);
        }


    }

    public void onSave(ActionEvent actionEvent) {
    }

    public void onLoad(ActionEvent actionEvent) {
    }

    public void onExit(ActionEvent actionEvent) {
    }

    public void onUndo(ActionEvent actionEvent) {
    }

    public void onRedo(ActionEvent actionEvent) {
    }

    public void onHints(ActionEvent actionEvent) {
    }

    public void onAbout(ActionEvent actionEvent) {
    }
}

package game;

import game.model.GameModel;
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
    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        System.out.printf("Click on square (%d,%d)%n", row, col);
        model.move(row, col);
    }

    public void onNewGame(ActionEvent actionEvent) {
        for (var i = 0; i < board.getRowCount(); i++) {
            for (var j = 0; j < board.getColumnCount(); j++) {

                model.board[i][j].set(NONE);
            }
        }
        Random random = new Random();
        int boardX = random.nextInt(8);
        int boardY = random.nextInt(8);
        int goalX = random.nextInt(8);
        int goalY = random.nextInt(8);
        if (boardX == 7){
            model.board[boardX][boardY].set(KNIGHT);
            model.board[boardX-1][boardY].set(KING);
            while ((goalX == boardX && goalY == boardY) || (goalX == boardX-1 && goalY == boardY)){
                goalX = random.nextInt(8);
                goalY = random.nextInt(8);
            }
            model.board[goalX][goalY].set(GOAL);
        }
        else {
            model.board[boardX][boardY].set(KNIGHT);
            model.board[boardX+1][boardY].set(KING);
            while ((goalX == boardX && goalY == boardY) || (goalX == boardX+1 && goalY == boardY)){
                goalX = random.nextInt(8);
                goalY = random.nextInt(8);
            }
            model.board[goalX][goalY].set(GOAL);
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

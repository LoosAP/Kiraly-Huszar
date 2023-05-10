package game;

import game.model.GameModel;
import javafx.beans.binding.ObjectBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

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
        return null;
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

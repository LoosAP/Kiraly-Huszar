package game.model;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class GameModel {

    public static int SIZE = 8;

    private ReadOnlyObjectWrapper<SquareStates>[][] board = new ReadOnlyObjectWrapper[SIZE][SIZE];
    public GameModel() {
        for (var i = 0; i < SIZE; i++) {
            for (var j = 0; j < SIZE; j++) {
                board[i][j] = new ReadOnlyObjectWrapper<SquareStates>(SquareStates.NONE);
            }
        }
    }

    public ReadOnlyObjectProperty<SquareStates> squareProperty(int i, int j) {
        return board[i][j].getReadOnlyProperty();
    }

    public SquareStates getSquareState(int i, int j){
        return board[i][j].get();
    }

    public void move(Integer row, Integer col) {
    }
}

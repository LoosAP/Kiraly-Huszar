package game.model;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class GameModel {

    public static int SIZE = 8;

    public ReadOnlyObjectWrapper<SquareStates>[][] board = new ReadOnlyObjectWrapper[SIZE][SIZE];
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
        /*board[row][col].set(
                switch (board[row][col].get()){
                    case KNIGHT -> null;
                    case NONE -> null;
                    case KING -> null;
                    case GOAL -> null;
                }
        );*/
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var i = 0; i < SIZE; i++) {
            for (var j = 0; j < SIZE; j++) {
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        var model = new GameModel();
        System.out.println(model);
    }
}

package game.model;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class GameModel {

    /**
     * The size of the board.
     */
    public static int SIZE = 8;
    /**
     * The valid moves of a knight.
     */

    private static final int[][] VALID_MOVES = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};

    private int kingRow;
    private int kingCol;

    private int knightRow;
    private int knightCol;

    private int goalRow;
    private int goalCol;

    /**
     * The board of the game.
     */
    private ReadOnlyObjectWrapper<SquareStates>[][] board = new ReadOnlyObjectWrapper[SIZE][SIZE];
    public GameModel() {
        initializeBoard();
    }

    /**
     * Initializes the board.
     */
    private void initializeBoard(){
        for (var i = 0; i < SIZE; i++) {
            for (var j = 0; j < SIZE; j++) {
                board[i][j] = new ReadOnlyObjectWrapper<SquareStates>(SquareStates.NONE);
            }
        }

        setKing(5,1);
        board[kingRow][kingCol] = new ReadOnlyObjectWrapper<SquareStates>(SquareStates.KING);

        setKnight(5,2);
        board[knightRow][knightCol] = new ReadOnlyObjectWrapper<SquareStates>(SquareStates.KNIGHT);

        setGoal(7,6);
        board[goalRow][goalCol] = new ReadOnlyObjectWrapper<SquareStates>(SquareStates.GOAL);
    }

    public ReadOnlyObjectProperty<SquareStates> squareProperty(int i, int j) {
        return board[i][j].getReadOnlyProperty();
    }

    public SquareStates getSquareState(int i, int j){
        return board[i][j].get();
    }
    public void setKing(Integer row, Integer col){
        kingRow = row;
        kingCol = col;
    }
    public void setKnight(Integer row, Integer col){
        knightRow = row;
        knightCol = col;
    }

    public void setGoal(Integer row, Integer col){
        goalRow = row;
        goalCol = col;
    }

    public void clearBoard(){
        for (var i = 0; i < SIZE; i++) {
            for (var j = 0; j < SIZE; j++) {
                board[i][j].set(SquareStates.NONE);
            }
        }
    }

    /**
     * Gets the row of a piece
     * @param piece the piece we want to get the row of
     */
    public int getRow(SquareStates piece){
        switch (piece){
            case KING -> {
                return kingRow;
            }
            case KNIGHT -> {
                return knightRow;
            }
            case GOAL -> {
                return goalRow;
            }
        }
        return -1;
    }

    /**
     * Gets the column of a piece
     * @param piece the piece we want to get the column of
     * @return
     */
    public int getCol(SquareStates piece){
        switch (piece){
            case KING -> {
                return kingCol;
            }
            case KNIGHT -> {
                return knightCol;
            }
            case GOAL -> {
                return goalCol;
            }
        }
        return -1;
    }

    public void setBoard(int x,int y, SquareStates state) {
        board[x][y].set(state);
    }

    /**
     * checks if the move we want to perform is valid
     * @param row the row we want to move to
     * @param col the column we want to move to
     * @param selectedPiece the piece we want to move
     */
    public boolean canMovePiece(Integer row,Integer col,SquareStates selectedPiece) {
        switch (selectedPiece){
            case KING -> {
                if ((row == kingRow && col == kingCol) || Math.abs(row - kingRow) > 1 || Math.abs(col - kingCol) > 1) {
                    return false;
                }
                return true;
            }
            case KNIGHT -> {
                if ((row == knightRow && col == knightCol) || row < 0 || col < 0 || row >= SIZE || col >= SIZE) {
                    return false;
                }
                for (int i = 0; i < VALID_MOVES.length; i++) {
                    int newRow = knightRow + VALID_MOVES[i][0];
                    int newCol = knightCol + VALID_MOVES[i][1];
                    if (newRow == row && newCol == col) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Checks if a piece is in check
     * @param selectedPiece the piece we want to check if it is in check
     */
    public boolean isInCheck(SquareStates selectedPiece) {
        switch (selectedPiece){
            case KING -> {
                // Check if king is in check by the knight
                for (int i = 0; i < VALID_MOVES.length; i++) {
                    int newRow = kingRow + VALID_MOVES[i][0];
                    int newCol = kingCol + VALID_MOVES[i][1];
                    if (newRow == knightRow && newCol == knightCol) {
                        return true;
                    }
                }
                return false;
            }
            case KNIGHT -> {
                // Check if knight is in check by the king
                if (Math.abs(kingRow - knightRow) <= 1 && Math.abs(kingCol - knightCol) <= 1) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Moves a piece to a new position
     * @param row the row we want to move to
     * @param col the column we want to move to
     * @param currentSquare the square we want to move to
     */
    public void move(Integer row, Integer col,SquareStates currentSquare) {


        switch (currentSquare) {
            case KING -> {
                // Move king to new position
                board[row][col].set(SquareStates.KING);
                // Clear old position
                board[kingRow][kingCol].set(SquareStates.NONE);
                // Update king's position
                setKing(row,col);
            }
            case KNIGHT -> {
                // Move knight to new position
                board[row][col].set(SquareStates.KNIGHT);
                // Clear old position
                board[knightRow][knightCol].set(SquareStates.NONE);
                // Update knight's position
                setKnight(row,col);
            }
            default -> {
            }
            // Invalid move - do nothing
        }
    }

    /**
     * Gets the positions of each piece and the goal, and stores them in a list
     */
    public List<Integer> getPositions(){
        return List.of(kingRow,kingCol,knightRow,knightCol,goalRow,goalCol);
    }
    /**
     * Sets the positions of each piece and the goal
     * @param readValue the list of positions we want to set
     */
    public void setPositions(ArrayList<Integer> readValue) {
        clearBoard();
        setKing(readValue.get(0), readValue.get(1));
        setBoard(kingRow,kingCol,SquareStates.KING);
        setKnight(readValue.get(2), readValue.get(3));
        setBoard(knightRow,knightCol,SquareStates.KNIGHT);
        setGoal(readValue.get(4), readValue.get(5));
        setBoard(goalRow,goalCol,SquareStates.GOAL);
    }

    /**
     * Prints the board
     */
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

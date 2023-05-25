package game;

import game.model.SquareStates;

/**
 * This class is used to keep track of the moves made by the pieces.
 * It is used to undo and redo moves.
 * @param currentRow the row of the piece that was moved
 * @param currentCol the column of the piece that was moved
 * @param prevPiece the piece that was previously in the square
 * @param prevRow the row of the piece that was previously in the square
 * @param prevCol the column of the piece that was previously in the square
 */
public class MoveTracker {
    private int currentRow;
    private int currentCol;
    private SquareStates prevPiece;
    private int prevRow;
    private int prevCol;

    public MoveTracker(int currentRow, int currentCol, SquareStates prevPiece, int prevRow, int prevCol) {

        this.currentRow = currentRow;
        this.currentCol = currentCol;
        this.prevPiece = prevPiece;
        this.prevRow = prevRow;
        this.prevCol = prevCol;
    }
    public MoveTracker(){
        this.currentRow = -1;
        this.currentCol = -1;
        this.prevPiece = SquareStates.NONE;
        this.prevRow = -1;
        this.prevCol = -1;
    }


    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public SquareStates getPrevPiece() {
        return prevPiece;
    }

    public int getPrevRow() {
        return prevRow;
    }

    public int getPrevCol() {
        return prevCol;
    }
}

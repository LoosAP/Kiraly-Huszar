import game.model.GameModel;
import game.model.SquareStates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GameModelTest {

    private GameModel gameModel;

    @BeforeEach
    public void setUp() {
        gameModel = new GameModel();
    }

    @Test
    public void testSquareProperty() {
        // Test that the squareProperty() method returns a valid property
        int row = 0;
        int col = 0;
        var property = gameModel.squareProperty(row, col);
        Assertions.assertNotNull(property);
    }

    @Test
    public void testGetSquareState() {
        // Test the initial state of the square
        int row = 0;
        int col = 0;
        var squareState = gameModel.getSquareState(row, col);
        Assertions.assertEquals(SquareStates.NONE, squareState);
    }

    @Test
    public void testSetKing() {
        // Test setting the king's position
        int row = 2;
        int col = 3;
        gameModel.setKing(row, col);
        var kingRow = gameModel.getRow(SquareStates.KING);
        var kingCol = gameModel.getCol(SquareStates.KING);
        Assertions.assertEquals(row, kingRow);
        Assertions.assertEquals(col, kingCol);
    }

    @Test
    public void testSetKnight() {
        // Test setting the knight's position
        int row = 4;
        int col = 5;
        gameModel.setKnight(row, col);
        var knightRow = gameModel.getRow(SquareStates.KNIGHT);
        var knightCol = gameModel.getCol(SquareStates.KNIGHT);
        Assertions.assertEquals(row, knightRow);
        Assertions.assertEquals(col, knightCol);
    }

    @Test
    public void testSetGoal() {
        // Test setting the goal's position
        int row = 6;
        int col = 7;
        gameModel.setGoal(row, col);
        var goalRow = gameModel.getRow(SquareStates.GOAL);
        var goalCol = gameModel.getCol(SquareStates.GOAL);
        Assertions.assertEquals(row, goalRow);
        Assertions.assertEquals(col, goalCol);

    }

    @Test
    public void testClearBoard() {
        // Test clearing the board
        int row = 2;
        int col = 3;
        gameModel.setKing(row, col);
        gameModel.clearBoard();
        var squareState = gameModel.getSquareState(row, col);
        Assertions.assertEquals(SquareStates.NONE, squareState);
    }

    @Test
    public void testCanMovePiece() {
        // Test valid and invalid moves for the king
        int row = 5;
        int col = 2;
        boolean validMove = gameModel.canMovePiece(row, col, SquareStates.KING);
        Assertions.assertTrue(validMove);

        row = 6;
        col = 6;
        validMove = gameModel.canMovePiece(row, col, SquareStates.KING);
        Assertions.assertFalse(validMove);
    }

    @Test
    public void testIsInCheck() {
        // Test if the king is in check by the knight
        gameModel.setKing(3, 3);
        gameModel.setKnight(1, 2);
        boolean isInCheck = gameModel.isInCheck(SquareStates.KING);
        Assertions.assertTrue(isInCheck);

        gameModel.setKnight(2, 2);
        isInCheck = gameModel.isInCheck(SquareStates.KING);
        Assertions.assertFalse(isInCheck);
    }

    @Test
    public void testMove() {
        // Test moving the king
        int newRow = 4;
        int newCol = 4;
        gameModel.move(newRow, newCol, SquareStates.KING);
        var kingRow = gameModel.getRow(SquareStates.KING);
        var kingCol = gameModel.getCol(SquareStates.KING);
        var squareState = gameModel.getSquareState(newRow, newCol);
        Assertions.assertEquals(newRow, kingRow);
        Assertions.assertEquals(newCol, kingCol);
        Assertions.assertEquals(SquareStates.KING, squareState);
    }

    @Test
    public void testGetPositionsAndSetPositions() {
        // Test getting and setting positions
        List<Integer> expectedPositions = List.of(2, 3, 4, 5, 6, 7);
        gameModel.setPositions(new ArrayList<>(expectedPositions));
        var positions = gameModel.getPositions();
        Assertions.assertEquals(expectedPositions, positions);
    }

    @Test
    public void testToString() {
        // Test the string representation of the board
        String expectedString = "0 0 0 0 0 0 0 0 \n" +
                "0 0 0 0 0 0 0 0 \n" +
                "0 0 0 0 0 0 0 0 \n" +
                "0 0 0 0 0 0 0 0 \n" +
                "0 0 0 0 0 0 0 0 \n" +
                "0 1 2 0 0 0 0 0 \n" +
                "0 0 0 0 0 0 0 0 \n" +
                "0 0 0 0 0 0 3 0 \n";
        var boardString = gameModel.toString();
        Assertions.assertEquals(expectedString, boardString);
    }
}
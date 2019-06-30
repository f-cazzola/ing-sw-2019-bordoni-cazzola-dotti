package it.polimi.ingsw.model;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class GameBoardTest {

    private final static int ROWS = 3;
    private final static int COLUMNS = 4;
    private final static int INF = 4;

    //test the correct search of visible squares - only some cases are tested

    @Test
    public void testGetVisibleSquares() {
        GameBoard board = new Match().getBoard();
        Square square;
        List<Square> list;
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLUMNS; j++) {
                square = board.getSquare(i, j);
                if (square != null) {
                    list = board.getVisibleSquares(square, INF, 0, false);
                    if (i == 0 && j == 0 || i == 1 && j == 0 || i == 0 && j == 2) {
                        assertTrue(list.contains(board.getSquare(0, 0)));
                        assertTrue(list.contains(board.getSquare(0, 1)));
                        assertTrue(list.contains(board.getSquare(0, 2)));
                        assertFalse(list.contains(board.getSquare(0, 3)));
                        assertTrue(list.contains(board.getSquare(1, 0)));
                        assertTrue(list.contains(board.getSquare(1, 1)));
                        assertTrue(list.contains(board.getSquare(1, 2)));
                        assertFalse(list.contains(board.getSquare(1, 3)));
                        assertFalse(list.contains(board.getSquare(2, 0)));
                        assertFalse(list.contains(board.getSquare(2, 1)));
                        assertFalse(list.contains(board.getSquare(2, 2)));
                        assertFalse(list.contains(board.getSquare(2, 3)));
                        assertEquals(list.size(), 6);
                    }
                    if (i == 1 && j == 1 || i == 2 && j == 1) {
                        assertFalse(list.contains(board.getSquare(0, 0)));
                        assertFalse(list.contains(board.getSquare(0, 1)));
                        assertFalse(list.contains(board.getSquare(0, 2)));
                        assertFalse(list.contains(board.getSquare(0, 3)));
                        assertTrue(list.contains(board.getSquare(1, 0)));
                        assertTrue(list.contains(board.getSquare(1, 1)));
                        assertTrue(list.contains(board.getSquare(1, 2)));
                        assertFalse(list.contains(board.getSquare(1, 3)));
                        assertFalse(list.contains(board.getSquare(2, 0)));
                        assertTrue(list.contains(board.getSquare(2, 1)));
                        assertTrue(list.contains(board.getSquare(2, 2)));
                        assertFalse(list.contains(board.getSquare(2, 3)));
                        assertEquals(list.size(), 5);
                    }
                    if (i == 2 && j == 2 || i == 2 && j == 3) {
                        assertFalse(list.contains(board.getSquare(0, 0)));
                        assertFalse(list.contains(board.getSquare(0, 1)));
                        assertFalse(list.contains(board.getSquare(0, 2)));
                        assertFalse(list.contains(board.getSquare(0, 3)));
                        assertFalse(list.contains(board.getSquare(1, 0)));
                        assertFalse(list.contains(board.getSquare(1, 1)));
                        assertFalse(list.contains(board.getSquare(1, 2)));
                        assertTrue(list.contains(board.getSquare(1, 3)));
                        assertFalse(list.contains(board.getSquare(2, 0)));
                        assertTrue(list.contains(board.getSquare(2, 1)));
                        assertTrue(list.contains(board.getSquare(2, 2)));
                        assertTrue(list.contains(board.getSquare(2, 3)));
                        assertEquals(list.size(), 4);
                    }
                    if (i == 1 && j == 2) {
                        assertTrue(list.contains(board.getSquare(0, 0)));
                        assertTrue(list.contains(board.getSquare(0, 1)));
                        assertTrue(list.contains(board.getSquare(0, 2)));
                        assertFalse(list.contains(board.getSquare(0, 3)));
                        assertTrue(list.contains(board.getSquare(1, 0)));
                        assertTrue(list.contains(board.getSquare(1, 1)));
                        assertTrue(list.contains(board.getSquare(1, 2)));
                        assertTrue(list.contains(board.getSquare(1, 3)));
                        assertFalse(list.contains(board.getSquare(2, 0)));
                        assertFalse(list.contains(board.getSquare(2, 1)));
                        assertFalse(list.contains(board.getSquare(2, 2)));
                        assertTrue(list.contains(board.getSquare(2, 3)));
                        assertEquals(list.size(), 8);
                    }
                }
            }
    }

    //test the correct search accessible directions from given position

    @Test
    public void testGetAccessibleDirections() {
        GameBoard board = new Match().getBoard();
        Square square;
        List<CardinalDirection> list;
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            square = board.getSquare(i / COLUMNS, i % COLUMNS);
            if (square != null) {
                list = board.getAccessibleDirection(square);
                if (i == 4 || i == 6 || i == 9 || i == 11)
                    assertTrue(list.contains(CardinalDirection.NORTH));
                else
                    assertFalse(list.contains(CardinalDirection.NORTH));
                if (i == 0 || i == 1 || i == 4 || i == 5 || i == 6 || i == 9 || i == 10)
                    assertTrue(list.contains(CardinalDirection.EAST));
                else
                    assertFalse(list.contains(CardinalDirection.EAST));
                if (i == 0 || i == 2 || i == 5 || i == 7)
                    assertTrue(list.contains(CardinalDirection.SOUTH));
                else
                    assertFalse(list.contains(CardinalDirection.SOUTH));
                if (i == 1 || i == 2 || i == 5 || i == 6 || i == 7 || i == 10 || i == 11)
                    assertTrue(list.contains(CardinalDirection.WEST));
                else
                    assertFalse(list.contains(CardinalDirection.WEST));
            }
        }
    }

    //test the correct visibility of players from given position - only one case is tested

    @Test
    public void testGetVisibleTarget() {
//        Match match = new Match();
//        Square square = match.getBoard().getSquare(0, 0);
//        ArrayList<Square> competitors;
//        Player one = new Player(match, null, null, null);
//        one.respawn(Color.RED);
//        one.move(CardinalDirection.NORTH);
//        one.getPosition().addPlayer(one);
//        Player two = new Player(match, null, null, null);
//        two.respawn(Color.RED);
//        two.getPosition().addPlayer(two);
//        Player three = new Player(match, null, null, null);
//        three.respawn(Color.BLUE);
//        three.getPosition().addPlayer(three);
//        Player four = new Player(match, null, null, null);
//        four.respawn(Color.YELLOW);
//        four.getPosition().addPlayer(four);
//        competitors = match.getBoard().getVisibleSquares(square, INF, 0, true);
//        assertEquals(competitors.contains(one.getPosition()), true);
//        assertEquals(competitors.contains(two.getPosition()), true);
//        assertEquals(competitors.contains(three.getPosition()), true);
//        assertEquals(competitors.contains(four.getPosition()), false);
    }

    //test the correct visibility through walls

    @Test
    public void testGetCardinalDirectionSquares() {
        Match match = new Match();
        Square square, temp;
        List<Square> list;
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLUMNS; j++) {
                square = match.getBoard().getSquare(i, j);
                if (square != null) {
                    list = match.getBoard().getCardinalDirectionSquares(square, INF, 0, true);
                    for (int r = 0; r < ROWS; r++)
                        for (int c = 0; c < COLUMNS; c++) {
                            temp = match.getBoard().getSquare(r, c);
                            if (temp != null)
                                assertEquals(list.contains(temp), r == i || c == j);
                        }
                }
            }
    }

    //test the correct square's reachability

    @Test
    public void testGetReachableSquares() {
        int maxMove = 6;
        Match match = new Match();
        Square square_one, square_two;
        List<Square> list_one;
        List<Square> list_two;
        for (int i = 0; i < 1; i++)
            for (int j = 0; j < 1; j++) {
                square_one = match.getBoard().getSquare(i, j);
                if (square_one != null) {
                    list_one = match.getBoard().getReachableSquare(square_one, maxMove);
                    for (int r = 2; r < 3; r++)
                        for (int c = 1; c < 2; c++) {
                            square_two = match.getBoard().getSquare(r, c);
                            if (square_two != null) {
                                list_two = match.getBoard().getReachableSquare(square_two, maxMove);
                                assertEquals(list_two.contains(square_one), list_one.contains(square_two));
                            }
                        }
                }
            }
    }

    @Test
    void getThirdSquareInTheSameDirection() {
        Match match = new Match();
        GameBoard gameBoard = match.getBoard();
        Square s1 = gameBoard.getSquare(2, 3);
        Square s2 = gameBoard.getSquare(1, 3);
        Square s3 = gameBoard.getThirdSquareInTheSameDirection(s1, s2, true);
        assertNull(s3);
        s1 = gameBoard.getSquare(2, 1);
        s2 = gameBoard.getSquare(1, 1);
        s3 = gameBoard.getThirdSquareInTheSameDirection(s1, s2, false);
        assertNull(s3);
        s3 = gameBoard.getThirdSquareInTheSameDirection(s1, s2, true);
        assertEquals(gameBoard.getSquare(0, 1), s3);
        s1 = gameBoard.getSquare(0, 2);
        s2 = gameBoard.getSquare(1, 2);
        s3 = gameBoard.getThirdSquareInTheSameDirection(s1, s2, true);
        assertEquals(gameBoard.getSquare(2, 2), s3);
    }
}
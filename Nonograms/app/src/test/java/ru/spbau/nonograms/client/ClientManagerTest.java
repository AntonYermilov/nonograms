package ru.spbau.nonograms.client;

import org.junit.Test;

import ru.spbau.nonograms.model.Image;

import static org.junit.Assert.assertEquals;

public class ClientManagerTest {
    private static Image heart = new Image(new int[][]{
            {48, 49, 48, 49, 48},
            {49, 48, 49, 48, 49},
            {49, 48, 48, 48, 49},
            {48, 49, 48, 49, 48},
            {48, 48, 49, 48, 48}
    }, 1, 48);

    private static Image house = new Image(new int[][]{
            {0, 0, 1, 0, 0},
            {0, 1, 0, 1, 0},
            {1, 0, 0, 0, 1},
            {1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1}
    }, 1, 0);

    private static Image unsolvableImage = new Image(new int [][]{
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {1, 0, 1, 0},
            {0, 1, 0, 1}
    }, 1, 0);

    private static Image teacup = new Image(new int[][]{
            {0, 0, 1, 0, 1, 0, 1, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 0, 0},
            {0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 0, 0},
            {1, 0, 1, 1, 1, 1, 1, 0, 0, 1},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0}
    }, 1, 0);

    @Test
    public void testSolveNonogramHeart() {
         assertEquals(true, ClientManager.solveNonogram(heart));
    }

    @Test
    public void testSolveNonogramHouse() {
        assertEquals(true, ClientManager.solveNonogram(house));
    }

    @Test
    public void testSolveNonogramUnsolvableImage() {
        assertEquals(false, ClientManager.solveNonogram(unsolvableImage));
    }

    @Test
    public void testSolveNonogramTeacup() {
        assertEquals(true, ClientManager.solveNonogram(teacup));
    }
}
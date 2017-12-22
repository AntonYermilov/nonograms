package ru.spbau.nonograms.client;

import org.junit.Test;
import static org.junit.Assert.*;

import ru.spbau.nonograms.logic.NonogramImage;

public class ClientManagerTest {
    private static NonogramImage heart = new NonogramImage(new int[][]{
            {0, 1, 0, 1, 0},
            {1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0},
            {0, 1, 0, 1, 0},
            {0, 0, 1, 0, 0}
    }, 0);

    private static NonogramImage house = new NonogramImage(new int[][]{
            {0, 0, 1, 0, 0},
            {0, 1, 0, 1, 0},
            {1, 0, 0, 0, 1},
            {1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1}
    }, 0);

    private static NonogramImage unsolvableImage = new NonogramImage(new int [][]{
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {1, 0, 1, 0},
            {0, 1, 0, 1}
    }, 0);

    private static NonogramImage teacup = new NonogramImage(new int[][]{
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
    }, 0);

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
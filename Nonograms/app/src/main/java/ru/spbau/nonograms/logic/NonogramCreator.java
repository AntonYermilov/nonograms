package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;
import java.util.HashMap;

/**
 * Special class for creating nonograms from images.
 */
class NonogramCreator {

    /**
     * Receives compressed image and creates nonogram of the specified size.
     *
     * @param image  specified image
     * @param width  width of nonogram
     * @param height height of nonogram
     *
     * @return nonogram image
     */
    static NonogramImage createNonogram(Bitmap image, int width, int height, int backgroundColor)
            throws NonogramImage.ColorOutOfRangeException {
        System.err.println("Background color: " + backgroundColor);
        System.err.println("Expected: " + width + "x" + height);
        System.err.println("Found: " + image.getWidth() + "x" + image.getHeight());

        int[][] field = new int[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                field[y][x] = image.getPixel(x, y);
            }
        }

        return new NonogramImage(field, backgroundColor);
    }

}

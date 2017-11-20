package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;
import java.util.HashMap;

/**
 * Special class for creating nonograms from images.
 */
class NonogramCreator {

    /**
     * Receives compressed image and creates nonogram of the specified size.
     * @param image  specified image
     * @param backgroundColor color of the background
     * @return nonogram image
     */
    static NonogramImage createNonogram(Bitmap image, int backgroundColor) {
        int[][] field = new int[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                field[y][x] = image.getPixel(x, y);
            }
        }

        return new NonogramImage(field, backgroundColor);
    }

}

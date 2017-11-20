package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Supports creating and checking nonograms.
 */
public class NonogramLogic {
    private static final int DIMENSION_MAX_SIZE = 1000;
    private static NonogramImage lastNonogram = null;

    /**
     * Creates nonogram with specified size and number of colors from specified image and
     * returns new one to show how nonogram looks like.
     *
     * @param image  specified image
     * @param width  number of nonogram's columns
     * @param height number of nonogram's rows
     * @param colors number of colors
     *
     * @return bitmap image that shows how created nonogram looks like.
     */
    public static Bitmap createNonogram(Bitmap image, int width, int height, int colors) {
        int previousWidth = image.getWidth();
        int previousHeight = image.getHeight();

        float scale = Math.max((float) previousWidth, (float) previousHeight) / DIMENSION_MAX_SIZE;
        if (scale > 1.0) {
            previousWidth = (int) (previousWidth / scale);
            previousHeight = (int) (previousHeight / scale);
        }

        System.err.println("Decreasing size");
        image = ImageTransformer.decreaseSize(image, width, height, false, true);
        System.err.println("Selecting colors");
        int backgroundColor = ImageTransformer.selectMainColors(image, colors, 2);
        System.err.println("Creating nonogram");
        lastNonogram = NonogramCreator.createNonogram(image, backgroundColor);
        System.err.println("Solving...");
        if (!canSolve(lastNonogram)) {
            lastNonogram = null;
        }
        System.err.println("Done");
        return ImageTransformer.increaseSize(image,
                previousWidth / width, previousHeight / height, true, false);
    }

    /**
     * Returns last created nonogram if it was created successfully or null otherwise.
     * @return last created nonogram
     */
    public static NonogramImage getLastNonogram() {
        return lastNonogram;
    }

    /**
     * Checks if specified field of RGB colors satisfies the correct nonogram.
     *
     * @param field    specified field, each element stores color of the corresponding cell
     * @param nonogram nonogram image to check with
     *
     * @return {@code true} if specified field satisfies correct nonogram; {@code false} otherwise
     */
    public static boolean checkNonogram(int[][] field, NonogramImage nonogram) {
        return NonogramChecker.check(field, nonogram);
    }

    /**
     * Checks if nonogram can be solved.
     * @param image specified nonogram image
     * @return {@code true} if nonogram can be solved; {@code false} otherwise
     */
    private static boolean canSolve(NonogramImage image) {
        boolean result = new MulticolorNonogramSolver(image).solve();
        System.err.println("Has solution: " + result);
        return result;
    }

}

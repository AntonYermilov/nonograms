package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Supports creating and checking nonograms.
 */
public class NonogramLogic {
    private static NonogramImage lastNonogram = null;

    /**
     * Creates nonogram with specified size and number of colors from specified image and
     * returns new one to show how nonogram looks like. Clears previous image to free memory.
     *
     * @param image  specified image
     * @param width  number of nonogram's columns
     * @param height number of nonogram's rows
     * @param colors number of colors
     *
     * @return nonogram image
     */
    public static Bitmap createNonogram(Bitmap image, int width, int height, int colors)
            throws NonogramImage.ColorOutOfRangeException {
        int backgroundColor = ImageTransformer.selectMainColors(image, colors, 2);

        int previousWidth = image.getWidth();
        int previousHeight = image.getHeight();

        image = resizeImage(image, width, height, true);
        ImageTransformer.selectMainColors(image, colors, 2);

        lastNonogram = NonogramCreator.createNonogram(image, width, height, colors);

        if (!canSolve(lastNonogram))
            return null;
        return resizeImage(image, previousWidth, previousHeight, true);
    }

    public static NonogramImage getLastNonogram() {
        return lastNonogram;
    }

    public static boolean checkNonogram(int[][] field, NonogramImage nonogram) {
        return NonogramChecker.check(field, nonogram);
    }

    /**
     * Checks if nonogram can be solved.
     * @param image specified nonogram image
     * @return {@code true} if nonogram can be solved; {@code false} otherwise
     */
    private static boolean canSolve(NonogramImage image) {
        return new MulticolorNonogramSolver(image).solve();
    }

    private static Bitmap resizeImage(Bitmap image, int newWidth, int newHeight,
                                       boolean removeOriginal) {
        float scaleWidth = ((float) newWidth) / image.getWidth();
        float scaleHeight = ((float) newHeight) / image.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedImage = Bitmap.createBitmap(image, 0, 0, newWidth, newHeight, matrix, true);
        if (removeOriginal) {
            image.recycle();
        }

        return resizedImage;
    }

}

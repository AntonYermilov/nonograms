package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;

/**
 * Supports creating and checking nonograms.
 */
public class NonogramLogic {
    private Bitmap image;
    private NonogramImage lastNonogram = null;

    /**
     * Constructs NonogramLogic class with specified bitmap image.
     * @param image specified bitmap image
     */
    public NonogramLogic(Bitmap image) {
        this.image = image.copy(image.getConfig(), true);
    }

    /**
     * Creates nonogram with specified size and number of colors from stored image.
     * Returns an image that shows how nonogram looks like.
     *
     * @param width number of nonogram's columns
     * @param height number of nonogram's rows
     * @param colors number of colors
     *
     * @return image that shows how nonogram looks like
     */
    public Bitmap showNonogram(int width, int height, int colors) {
        Bitmap blackAndWhiteImage = image.copy(image.getConfig(), true);
        int backgroundColor = ImageTransformer.selectMainColors(blackAndWhiteImage, colors);
        lastNonogram = NonogramCreator.createNonogram(blackAndWhiteImage, width, height, backgroundColor);

        if (!canSolve(lastNonogram))
            return null;
        return blackAndWhiteImage;
    }

    /**
     * Returns last created nonogram.
     * @return last created nonogram
     */
    public NonogramImage getLastNonogram() {
        return lastNonogram;
    }

    /**
     * Checks if nonogram can be solved.
     * @param image specified nonogram image
     * @return {@code true} if nonogram can be solved; {@code false} otherwise
     */
    private boolean canSolve(NonogramImage image) {
        MulticolorNonogramSolver solver = new MulticolorNonogramSolver(image);
        return solver.solve();
    }

}

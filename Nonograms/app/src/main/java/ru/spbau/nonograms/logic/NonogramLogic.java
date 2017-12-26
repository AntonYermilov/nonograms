package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;

import java.util.logging.Level;
import java.util.logging.Logger;

import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.model.Image;

/**
 * Supports creating and checking nonograms.
 */
public class NonogramLogic {
    private static final int DIMENSION_MAX_SIZE = 720;
    private static CurrentCrosswordState lastNonogram = null;

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
        if (image.getWidth() < width || image.getHeight() < height) {
            lastNonogram = null;
            return null;
        }

        int previousHeight = image.getHeight();
        int previousWidth = image.getWidth();

        float scale = Math.max((float) previousHeight, (float) previousWidth) / DIMENSION_MAX_SIZE;
        if (scale > 1.0) {
            previousHeight = (int) (previousHeight / scale);
            previousWidth = (int) (previousWidth / scale);
        }

        Logger.getGlobal().logp(Level.INFO, "NonogramLogic", "createNonogram", "Decreasing size...");
        image = ImageTransformer.decreaseSize(image, width, height, false);

        Logger.getGlobal().logp(Level.INFO, "NonogramLogic", "createNonogram", "Creating nonogram...");

        Image nonogram = Controller.createImageOnServer(new Image(image, colors));
        lastNonogram = nonogram.toCurrentCrosswordState();
        image = nonogram.toBitmap();

        Logger.getGlobal().logp(Level.INFO, "NonogramImage", "createNonogram", "Increasing size...");
        return ImageTransformer.increaseSize(image,
                previousWidth / width, previousHeight / height, true);
    }

    /**
     * Returns last created nonogram if it was created successfully or null otherwise.
     * @return last created nonogram
     */
    public static CurrentCrosswordState getLastNonogram() {
        return lastNonogram;
    }

    /**
     * Checks if specified nonogram is solved correctly
     * @param state current nonogram state
     * @return {@code true} if specified nonogram is solved correctly; {@code false} otherwise
     */
    public static boolean checkNonogram(CurrentCrosswordState state) {
        return NonogramChecker.check(state);
    }
}

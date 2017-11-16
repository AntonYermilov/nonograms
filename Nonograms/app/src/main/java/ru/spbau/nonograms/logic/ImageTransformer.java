package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.Arrays;

/**
 * Selects some main colors in image and transforms it.
 * Temporarily supports transforming to black'n'white image only.
 */
class ImageTransformer {
    static final int MIN_GRAY_SCALE_TYPE = 0;
    static final int MAX_GRAY_SCALE_TYPE = 2;

    /**
     * Grayscales image and compresses it by selecting main colors in it.
     * Returns the color of the background in RGB format.
     *
     * @param image      specified image
     * @param mainColors number of main colors
     *
     * @return the color of the background in RGB format
     */
    static int selectMainColors(Bitmap image, int mainColors, int grayScaleType) {
        int backgroundColor = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getPixel(x, y);
                int a = Color.alpha(color);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                int grayTone = grayScale(r, g, b, grayScaleType);
                grayTone = getOptimalColor(grayTone, mainColors);

                backgroundColor = Math.max(backgroundColor, grayTone);
                image.setPixel(x, y, getARGB(a, grayTone, grayTone, grayTone));
            }
        }
        return getARGB(255, backgroundColor, backgroundColor, backgroundColor);
    }

    /**
     * Creates new image with the size, multiplied in both directions by the specified value.
     *
     * @param image          specified image
     * @param zoomWidth      width multiplier
     * @param zoomHeight     height multiplier
     * @param removeOriginal if {@code true}, deletes the specified image
     * @param mutable        if {@code true}, makes the new image mutable
     *
     * @return new image with decreased size
     */
    static Bitmap increaseSize(Bitmap image, int zoomWidth, int zoomHeight,
                               boolean removeOriginal, boolean mutable) {
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = width * zoomWidth;
        int newHeight = height * zoomHeight;

        int[] pixels = new int[newWidth * newHeight];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int row = y * zoomHeight; row < (y + 1) * zoomHeight; row++) {
                    int startPos = row * newWidth + x * zoomWidth;
                    int endPos = row * newWidth + (x + 1) * zoomWidth;
                    Arrays.fill(pixels, startPos, endPos, image.getPixel(x, y));
                }
            }
        }

        Bitmap newImage = Bitmap.createBitmap(pixels, newWidth, newHeight, image.getConfig());
        if (removeOriginal) {
            image.recycle();
        }
        if (mutable) {
            return newImage.copy(newImage.getConfig(), true);
        }
        return newImage;
    }

    /**
     * Creates new image with the size, decreased to the specified one.
     *
     * @param image          specified image
     * @param newWidth       width if the new image
     * @param newHeight      height of the new image
     * @param removeOriginal if {@code true}, deletes the specified image
     * @param mutable        if {@code true}, makes the new image mutable
     *
     * @return new image with decreased size
     */
    static Bitmap decreaseSize(Bitmap image, int newWidth, int newHeight,
                               boolean removeOriginal, boolean mutable) {
        float scaleWidth = ((float) newWidth) / image.getWidth();
        float scaleHeight = ((float) newHeight) / image.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newImage = Bitmap.createBitmap(image, 0, 0,
                image.getWidth(), image.getHeight(), matrix, true);
        if (removeOriginal) {
            image.recycle();
        }
        if (mutable) {
            return newImage.copy(newImage.getConfig(), true);
        }
        return newImage;
    }

    /**
     * Returns gray tone that corresponds to the specified color.
     *
     * @param r    red component of color
     * @param g    green component of color
     * @param b    blue component of color
     * @param type type of grayscale
     *
     * @return gray tone that corresponds to the specified color
     */
    private static int grayScale(int r, int g, int b, int type) {
        if (type == 1) {
            return Math.min(255, (int) (0.2162 * r + 0.7152 * g + 0.0722 * b));
        }
        if (type == 2) {
            return Math.min(255, (int) (0.299 * r + 0.587 * g + 0.114 * b));
        }
        return Math.max(r, Math.max(g, b));
    }

    /**
     * Selects optimal color from the number of main ones.
     *
     * @param color      specified color
     * @param mainColors number of main colors
     *
     * @return optimal color
     */
    private static int getOptimalColor(int color, int mainColors) {
        int nearest = 0;
        for (int i = 0; i < mainColors; i++) {
            if (255 * i / mainColors <= color) {
                nearest = i;
            }
        }
        return Math.min(255, (256 + 255 / (mainColors - 1)) * nearest / mainColors);
    }

    /**
     * Returns color in argb type.
     *
     * @param a alpha component of color
     * @param r red component of color
     * @param g green component of color
     * @param b blue component of color
     *
     * @return color in argb type
     */
    private static int getARGB(int a, int r, int g, int b) {
        return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

}
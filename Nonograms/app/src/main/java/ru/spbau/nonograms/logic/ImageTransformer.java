package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.Arrays;

/**
 * Selects some main colors in image and transforms it.
 * Temporarily supports transforming to black'n'white image only.
 */
class ImageTransformer {

    /**
     * Creates new image with the size, multiplied in both directions by the specified value.
     *
     * @param image          specified image
     * @param zoomWidth      width multiplier
     * @param zoomHeight     height multiplier
     * @param removeOriginal if {@code true}, deletes the specified image
     * @return new image with decreased size
     */
    static Bitmap increaseSize(Bitmap image, int zoomWidth, int zoomHeight, boolean removeOriginal) {
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
        return newImage;
    }

    /**
     * Creates new image with the size, decreased to the specified one.
     *
     * @param image          specified image
     * @param newWidth       width if the new image
     * @param newHeight      height of the new image
     * @param removeOriginal if {@code true}, deletes the specified image
     * @return new image with decreased size
     */
    static Bitmap decreaseSize(Bitmap image, int newWidth, int newHeight, boolean removeOriginal) {
        float scaleWidth = ((float) newWidth) / image.getWidth();
        float scaleHeight = ((float) newHeight) / image.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newImage = Bitmap.createBitmap(image, 0, 0,
                image.getWidth(), image.getHeight(), matrix, true);
        if (removeOriginal) {
            image.recycle();
        }
        return newImage;
    }

}
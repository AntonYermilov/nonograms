package ru.spbau.nonograms.logic;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

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
     * @return the color of the background in RGB format
     */
    static int selectMainColors(Bitmap image, int mainColors) {
        int backgroundColor = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getPixel(x, y);
                int a = Color.alpha(color);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                int grayTone = grayScale(r, g, b, 0);
                grayTone = getOptimalColor(grayTone, mainColors);

                backgroundColor = Math.max(backgroundColor, grayTone);
                image.setPixel(x, y, getARGB(a, grayTone, grayTone, grayTone));
            }
        }
        return getARGB(1, backgroundColor, backgroundColor, backgroundColor);
    }

    private static int grayScale(int r, int g, int b, int type) {
        if (type == 1) {
            return Math.min(255, (int) (0.2162 * r + 0.7152 * g + 0.0722 * b));
        }
        if (type == 2) {
            return Math.min(255, (int) (0.299 * r + 0.587 * g + 0.114 * b));
        }
        return Math.max(r, Math.max(g, b));
    }

    private static int getOptimalColor(int color, int mainColors) {
        int nearest = 0;
        for (int i = 0; i < mainColors; i++) {
            if (255 * i / mainColors <= color) {
                nearest = i;
            }
        }
        return Math.min(255, (256 + 255 / (mainColors - 1)) * nearest / mainColors);
    }

    private static int getARGB(int a, int r, int g, int b) {
        return ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

}

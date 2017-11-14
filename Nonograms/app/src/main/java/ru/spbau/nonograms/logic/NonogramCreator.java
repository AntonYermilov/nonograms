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
     * @param width  width of nonogram
     * @param height height of nonogram
     * @return nonogram image
     */
    static NonogramImage createNonogram(Bitmap image, int width, int height, int backgroundColor) {
        int sizeX = image.getWidth() / width;
        int sizeY = image.getHeight() / height;

        int[][] field = new int[height][width];

        for (int x1 = 0; x1 < image.getWidth(); x1 += sizeX) {
            for (int y1 = 0; y1 < image.getHeight(); y1 += sizeY) {
                int x2 = Math.min(image.getWidth(), x1 + sizeX);
                int y2 = Math.min(image.getHeight(), y1 + sizeY);

                int color = getMajorColor(image, x1, y1, x2, y2);
                setMajorColor(image, x1, y1, x2, y2, color);

                if (x1 / sizeX < width && y1 / sizeY < height) {
                    field[y1 / sizeY][x1 / sizeX] = color;
                }
            }
        }

        NonogramImage nonogram = null;
        try {
            nonogram = new NonogramImage(field, backgroundColor);
        } catch (NonogramImage.ColorOutOfRangeException e) {
            System.err.println(e.getMessage());
        }
        return nonogram;
    }

    private static int getMajorColor(Bitmap image, int x1, int y1, int x2, int y2) {
        HashMap<Integer, Integer> count = new HashMap<>();
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                int color = image.getPixel(x, y);
                count.put(color, count.containsKey(color) ? count.get(color) + 1 : 1);
            }
        }

        int major = -1;
        for (int color : count.keySet()) {
            if (major == -1 || count.get(color) > count.get(major)) {
                major = color;
            }
        }

        return major;
    }

    private static void setMajorColor(Bitmap image, int x1, int y1, int x2, int y2, int color) {
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                image.setPixel(x, y, color);
            }
        }
    }

}

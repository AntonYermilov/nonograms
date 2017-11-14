package ru.spbau.nonograms.logic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for storing information about nonogram.
 * Stores nonogram's size and list of numbers for each row and column.
 */
public class NonogramImage {
    public static final int MAX_COLORS = 7;

    private int height;
    private int width;
    private int colors;
    private ArrayList<Segment>[] rows;
    private ArrayList<Segment>[] columns;

    private int backgroundColor;
    private Color[] usedColors;

    /**
     * Receives the resulting look of nonogram as two-dimensional array
     * of RGB colors and color of background. Uses them to create nonogram.
     *
     * @param field           two-dimensional array that stores nonogram look
     * @param backgroundColor specified color of the background
     *
     * @throws ColorOutOfRangeException if there are more than {@code MAX_COLORS}
     * colors, not counting the background color
     */
    NonogramImage(int[][] field, int backgroundColor) throws ColorOutOfRangeException {
        height = field.length;
        width = field[0].length;
        colors = 0;

        this.backgroundColor = backgroundColor;
        System.err.println(backgroundColor);

        HashMap<Integer, Integer> colorId = new HashMap<>();
        colorId.put(backgroundColor, 0);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!colorId.containsKey(field[i][j])) {
                    colorId.put(field[i][j], ++colors);
                }
            }
        }

        if (colors > MAX_COLORS) {
            throw new ColorOutOfRangeException();
        }

        usedColors = new Color[colorId.size()];
        int nextColor = 0;
        for (int rgbColor : colorId.keySet()) {
            System.err.print(rgbColor + " ");
            usedColors[nextColor++] = new Color(colorId.get(rgbColor), rgbColor);
        }
        System.err.println();

        rows = new ArrayList[height];
        for (int i = 0; i < height; i++) {
            rows[i] = new ArrayList<>();
            for (int l = 0, r = 0; r < width; l = r) {
                while (r < width && field[i][l] == field[i][r]) {
                    r++;
                }
                if (field[i][l] != backgroundColor) {
                    rows[i].add(new Segment(r - l, colorId.get(field[i][l]), field[i][l]));
                }
            }
        }

        columns = new ArrayList[width];
        for (int i = 0; i < width; i++) {
            columns[i] = new ArrayList<>();
            for (int l = 0, r = 0; r < height; l = r) {
                while (r < height && field[l][i] == field[r][i]) {
                    r++;
                }
                if (field[l][i] != backgroundColor) {
                    columns[i].add(new Segment(r - l, colorId.get(field[l][i]), field[l][i]));
                }
            }
        }
    }

    /**
     * Returns number of rows in nonogram.
     * @return number of rows in nonogram
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns number of columns in nonogram.
     * @return number of columns in nonogram
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns number of colors in nonogram.
     * @return number of colors in nonogram
     */
    public int getColors() {
        return colors;
    }

    /**
     * Returns sequence of blocks that correspond to the specified row.
     * @param row specified row
     * @return sequence of blocks that correspond to the specified row
     */
    public ArrayList<Segment> getRow(int row) {
        return new ArrayList<>(rows[row]);
    }

    /**
     * Returns sequence of blocks that correspond to the specified column.
     * @param column specified column
     * @return sequence of blocks that correspond to the specified column
     */
    public ArrayList<Segment> getColumn(int column) {
        return new ArrayList<>(columns[column]);
    }

    /**
     * Returns the background color.
     * @return the background color
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the array of used in nonogram colors.
     * @return the array of used in nonogram colors
     */
    public Color[] getUsedColors() {
        return usedColors;
    }

    /**
     * Stores information about each block of sequential cells of same color.
     */
    public static class Segment {
        private int size;
        private Color color;

        /**
         * Receives the size of block of sequential cells of same color, the type of
         * the color and it's RGB value and creates segment, that stores all this
         * information.
         * The type of the color is a special number between {@code 0} and {@code colors}
         * that identifies it's RGB value.
         *
         * @param size      the size of block
         * @param colorType the type of color
         * @param rgbColor  the color in RGB format
         */
        Segment(int size, int colorType, int rgbColor) {
            this.size = size;
            this.color = new Color(colorType, rgbColor);
        }

        /**
         * Returns the size of the segment.
         * @return the size of the segment
         */
        public int getSize() {
            return size;
        }

        /**
         * Returns the type of the color.
         * @return the type of the color
         */
        public int getColorType() {
            return color.colorType;
        }

        /**
         * Returns RGB value of the color.
         * @return RGB value of the color
         */
        public int getRGBColor() {
            return color.rgbColor;
        }
    }

    /**
     * Stores information about color.
     */
    public static class Color {
        private int colorType;
        private int rgbColor;

        /**
         * Creates color that stores it's type and RGB value.
         * @param colorType the type of the color
         * @param rgbColor  RGB value of the color
         */
        Color(int colorType, int rgbColor) {
            this.colorType = colorType;
            this.rgbColor = rgbColor;
        }

        /**
         * Returns the type of the color.
         * @return the type of the color
         */
        public int getColorType() {
            return colorType;
        }

        /**
         * Returns RGB value of the color.
         * @return RGB value of the color
         */
        public int getRGBColor() {
            return rgbColor;
        }
    }

    /**
     * Throws if more than {@code MAX_COLORS} colors used in nonogram.
     */
    public static class ColorOutOfRangeException extends Exception {
        ColorOutOfRangeException() {
            super("Incorrect field. Not more than " + MAX_COLORS + "colors, not counting" +
                    "the background color, can be used in nonogram.");
        }
    }
}

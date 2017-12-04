package ru.spbau.nonograms.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for storing information about nonogram.
 * Stores nonogram's size and list of numbers for each row and column.
 */
public class NonogramImage implements Serializable {
    public static final int MAX_COLORS = 7;

    private int height;
    private int width;
    private int colors;
    private List<Segment>[] rows;
    private List<Segment>[] columns;

    private int backgroundColor;
    private int[] usedRGBColors;
    private HashMap<Integer, Integer> colorId;
    private int[] colorRGB;

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
    public NonogramImage(int[][] field, int backgroundColor) {
        height = field.length;
        width = field[0].length;
        colors = 0;

        this.backgroundColor = backgroundColor;

        colorId = new HashMap<>();

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

        colorRGB = new int[colors + 1];
        usedRGBColors = new int[colorId.size()];
        int nextColor = 0;
        for (int rgbColor : colorId.keySet()) {
            colorRGB[colorId.get(rgbColor)] = rgbColor;
            usedRGBColors[nextColor++] = rgbColor;
        }

        rows = new List[height];
        for (int i = 0; i < height; i++) {
            rows[i] = new ArrayList<>();
            for (int l = 0, r = 0; r < width; l = r) {
                while (r < width && field[i][l] == field[i][r]) {
                    r++;
                }
                if (field[i][l] != backgroundColor) {
                    rows[i].add(new Segment(r - l, colorId.get(field[i][l])));
                }
            }
        }

        columns = new List[width];
        for (int i = 0; i < width; i++) {
            columns[i] = new ArrayList<>();
            for (int l = 0, r = 0; r < height; l = r) {
                while (r < height && field[l][i] == field[r][i]) {
                    r++;
                }
                if (field[l][i] != backgroundColor) {
                    columns[i].add(new Segment(r - l, colorId.get(field[l][i])));
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
    public List<Segment> getRow(int row) {
        return new ArrayList<>(rows[row]);
    }

    /**
     * Returns sequence of blocks that correspond to the specified column.
     * @param column specified column
     * @return sequence of blocks that correspond to the specified column
     */
    public List<Segment> getColumn(int column) {
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
    public int[] getUsedColors() {
        return usedRGBColors;
    }

    /**
     * Returns color id by it's RGB value.
     * @param rgbColor RGB value of the color
     * @return color id by it's RGB value
     */
    public int getColorId(int rgbColor) {
        return colorId.get(rgbColor);
    }

    /**
     * Returns RGB value of the color by it's id.
     * @param colorId id of the color
     * @return RGB value of the color by it's id
     */
    public int getRGBColor(int colorId) {
        return colorRGB[colorId];
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    /**
     * Stores information about each block of sequential cells of same color.
     */
    public class Segment {
        private int size;
        private int color;

        /**
         * Receives the size of block of sequential cells of same color, the type of
         * the color and it's RGB value and creates segment, that stores all this
         * information.
         * The type of the color is a special number between {@code 0} and {@code colors}
         * that identifies it's RGB value.
         *
         * @param size      the size of block
         * @param colorType the type of color
         */
        public Segment(int size, int colorType) {
            this.size = size;
            this.color = colorType;
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
            return color;
        }
    }

    /**
     * Throws if more than {@code MAX_COLORS} colors used in nonogram.
     */
    public static class ColorOutOfRangeException extends IllegalArgumentException {
        ColorOutOfRangeException() {
            super("Error when creating image. Not more than " + MAX_COLORS + "colors, not counting" +
                    "the background color, can be used in nonogram. All color types, if used," +
                    "should be sequent, background color should have type equals to 0");
        }
    }
}

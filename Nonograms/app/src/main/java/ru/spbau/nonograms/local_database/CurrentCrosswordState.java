package ru.spbau.nonograms.local_database;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Stores current state of the crossword.
 * Current field and useful info to display crossword.
 */
public class CurrentCrosswordState implements Serializable {

    public static final int FILLED_CELL = 2;

    private int backgroundColor;
    private int height;
    private int width;

    private ColoredValue[][] rows;
    private int rowsMax;
    private int columnsMax;
    private ColoredValue[][] columns;
    private int colors[];
    private ColoredValue[][] field;

    /**
     * Creates a current crossword state object, using info about
     * a crossword itself (which numbers of which colors are in the rows and columns), an array of used colors
     * and a last field state. (optional)
     * @param rows numbers and their colors in rows
     * @param columns numbers and their color in columns
     * @param colors all columns used in crossword
     * @param lastField last state field of the crossword. Can be {@code null}
     */
    public CurrentCrosswordState(ColoredValue[][] rows, ColoredValue[][] columns, int colors[],
                                 int backgroundColor, ColoredValue[][] lastField) {
        this.backgroundColor = backgroundColor;
        height = rows.length;
        width = columns.length;
        this.rows = copyDoubleArray(rows);
        this.columns = copyDoubleArray(columns);
        if (lastField != null) {
            field = copyDoubleArray(lastField);
        } else {
            field = new ColoredValue[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    field[i][j] = new ColoredValue(0, Color.WHITE);
                }
            }
        }
        this.colors = Arrays.copyOf(colors, colors.length);
        for (ColoredValue[] row : rows) {
            rowsMax = Math.max(rowsMax, row.length);
        }
        for (ColoredValue[] column : columns) {
            columnsMax = Math.max(columnsMax, column.length);
        }
    }

    /**
     * Returns background color of a crossword.
     * @return background color of a crossword.
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns array of colors used in the crossword.
     * @return array of colors used in the crossword.
     */
    public int[] getColors() {
        return colors;
    }

    /**
     * Returns height of the crossword.
     * @return height of the crossword.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns width of the crossword.
     * @return width of the crossword.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns value in the field -- color and number. (ColoredValue)
     * @param i a row to look at
     * @param j a column to look at
     * @return value in the field -- color and number. (ColoredValue)
     */
    public ColoredValue getField(int i, int j) {
        return field[i][j];
    }

    /**
     * Sets a colored value in the field.
     * @param i a row to look at
     * @param j a column to look at
     * @param value to store into the postion
     */
    public void setField(int i, int j, ColoredValue value) {
        field[i][j] = value;
    }

    /**
     * Returns information about rows.
     * @return information about rows.
     */
    public ColoredValue[][] getRows() {
        return rows;
    }

    /**
     * Returns information about columns.
     * @return information about columns.
     */
    public ColoredValue[][] getColumns() {
        return columns;
    }

    /**
     * Returns maximum length of information array about the rows.
     * @return maximum length of information array about the rows.
     */
    public int getRowsMax() {
        return rowsMax;
    }

    /**
     * Returns maximum length of information array about the columns.
     * @return maximum length of information array about the columns.
     */
    public int getColumnsMax() {
        return columnsMax;
    }

    private static ColoredValue[][] copyDoubleArray(ColoredValue[][] arr) {
        ColoredValue[][] res = new ColoredValue[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            res[i] = new ColoredValue[arr[i].length];
            for (int j = 0; j < arr[i].length; j++) {
                res[i][j] = new ColoredValue(arr[i][j].getValue(), arr[i][j].getColor());
            }
        }
        return res;
    }

    /** Clears all the field of a crossword */
    public void clearField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field[i][j] = new ColoredValue(0, Color.WHITE);
            }
        }
    }

    /** A class to store color and value */
    public static class ColoredValue implements Serializable {
        private int value;
        private int color;

        public ColoredValue(int value, int color) {
            this.value = value;
            this.color = color;
        }

        public int getValue() {
            return value;
        }

        public int getColor() {
            return color;
        }
    }
}

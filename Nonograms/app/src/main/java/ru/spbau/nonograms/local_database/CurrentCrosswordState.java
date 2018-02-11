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

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int[] getColors() {
        return colors;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public ColoredValue getField(int i, int j) {
        return field[i][j];
    }

    public void setField(int i, int j, ColoredValue value) {
        field[i][j] = value;
    }

    public ColoredValue[][] getRows() {
        return rows;
    }

    public ColoredValue[][] getColumns() {
        return columns;
    }

    public int getRowsMax() {
        return rowsMax;
    }

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

    public void clearField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field[i][j] = new ColoredValue(0, Color.WHITE);
            }
        }
    }

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

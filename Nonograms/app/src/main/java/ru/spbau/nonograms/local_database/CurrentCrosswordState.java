package ru.spbau.nonograms.local_database;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * Stores current state of the crossword.
 * Current field and useful info to display crossword.
 */
public class CurrentCrosswordState implements Serializable {

    public static final int FILLED_CELL = 2;

    private int height;
    private int width;

    private ColoredValue[][] rows;
    private int rowsMax;
    private int columnsMax;
    private ColoredValue[][] columns;
    private int colors[];
    private ColoredValue[][] field;

    public CurrentCrosswordState(ColoredValue[][] rows, ColoredValue[][] columns, int colors[], ColoredValue[][] lastField) {
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
        for (int i = 0; i < rows.length; i++) {
            rowsMax = Math.max(rowsMax, rows[i].length);
        }
        for (int i = 0; i < columns.length; i++) {
            columnsMax = Math.max(columnsMax, columns[i].length);
        }
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

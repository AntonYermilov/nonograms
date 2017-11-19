package ru.spbau.nonograms.local_database;

/**
 * Stores current state of the crossword.
 * Current field and useful info to display crossword.
 */
public class CurrentCrosswordState {
    private int height;
    private int width;

    private int[][] rows;
    private int rowsMax;
    private int columnsMax;
    private int[][] columns;

    private int[][] field;

    public CurrentCrosswordState(int[][] rows, int[][] columns, int[][] lastField) {
        height = rows.length;
        width = columns.length;
        this.rows = copyDoubleArray(rows);
        this.columns = copyDoubleArray(columns);
        if (lastField != null) {
            field = copyDoubleArray(lastField);
        } else {
            field = new int[width][height];
        }
        for (int i = 0; i < rows.length; i++) {
            rowsMax = Math.max(rowsMax, rows[i].length);
        }
        for (int i = 0; i < columns.length; i++) {
            columnsMax = Math.max(columnsMax, columns[i].length);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getField(int i, int j) {
        return field[i][j];
    }

    public void setField(int i, int j, int value) {
        field[i][j] = value;
    }

    public int[][] getRows() {
        return rows;
    }

    public int[][] getColumns() {
        return columns;
    }

    public int getRowsMax() {
        return rowsMax;
    }

    public int getColumnsMax() {
        return columnsMax;
    }

    private static int[][] copyDoubleArray(int[][] arr) {
        int[][] res = new int[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            res[i] = new int[arr[i].length];
            for (int j = 0; j < arr[i].length; j++) {
                res[i][j] = arr[i][j];
            }
        }
        return res;
    }
}

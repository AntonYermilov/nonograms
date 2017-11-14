package ru.spbau.nonograms.logic;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Solver for Japanese crosswords.
 * Supports solving of multicolor crosswords.
 */
class MulticolorNonogramSolver {
    private static int EXACTLY_WHITE = -1;
    private static int UNKNOWN = 0;


    private NonogramImage image;

    /**
     * For each cell stores -1 if cell is exactly white, some positive number
     * if cell is exactly painted some color (this number determines the color
     * of cell), or 0 otherwise.
     */
    private int field[][];

    private boolean isSolved = false;
    private boolean isCorrect = false;

    /**
     * Constructs nonogram solver from nonogram image.
     * @param image specified nonogram image
     */
    MulticolorNonogramSolver(NonogramImage image) {
        this.image = image;
        this.field = new int[image.getHeight()][image.getWidth()];
    }

    /**
     * Prints nonogram. Each cell of the printed nonogram stores type of color or "?"
     * if this color is undefined.
     *
     * Was used for debugging nonogram solver.
     */
    void print() {
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (field[i][j] == EXACTLY_WHITE) {
                    System.err.print(" ");
                }
                if (field[i][j] == UNKNOWN) {
                    System.err.print("?");
                }
                if (field[i][j] > 0) {
                    System.err.print(field[i][j]);
                }
            }
            System.err.println();
        }
    }

    /**
     * Tries to fill as many cells as possible.
     * Returns {@code true} if all cells are filled, or {@code false} otherwise.
     * @return {@code true} if all cells are filled; {@code false} otherwise
     */
    boolean solve() {
        if (isSolved) {
            return isCorrect;
        }

        int filled = 0;
        while (!isSolved) {
            isSolved = true;
            for (int row = 0; row < image.getHeight(); row++) {
                int added = fillRow(row, image.getRow(row));
                filled += added;
                isSolved &= added == 0;
            }
            for (int column = 0; column < image.getWidth(); column++) {
                int added = fillColumn(column, image.getColumn(column));
                filled += added;
                isSolved &= added == 0;
            }
        }

        isCorrect = filled == image.getHeight() * image.getWidth();
        return isCorrect;
    }

    /**
     * Tries to fill a row. Marks each cell depending on it's type:
     * "filled", "can be filled" or "can not be filled".
     * @param row      nonogram's row
     * @param sequence sequence of segments' sizes and colors
     * @return number of cells that were marked on this step
     */
    private int fillRow(int row, ArrayList<NonogramImage.Segment> sequence) {
        int filledNew = 0;

        boolean isColored[][] = new boolean[image.getColors() + 1][image.getWidth()];
        boolean isWhite[] = new boolean[image.getWidth()];
        for (int column = 0; column < image.getWidth(); column++) {
            isWhite[column] = field[row][column] == EXACTLY_WHITE;
            for (int color = 1; color <= image.getColors(); color++) {
                isColored[color][column] = field[row][column] == color;
                isColored[0][column] |= isColored[color][column];
            }
        }

        int updatedColors[] = getUpdatedColors(sequence, isColored, isWhite);
        for (int column = 0; column < image.getWidth(); column++) {
            filledNew += updatedColors[column] != field[row][column] ? 1 : 0;
            field[row][column] = updatedColors[column];
        }

        return filledNew;
    }

    /**
     * Tries to fill a column. Marks each cell depending on it's type:
     * "filled", "can be filled" or "can not be filled".
     * @param column   nonogram's column
     * @param sequence sequence of segments' sizes and colors
     * @return number of cells that were marked on this step
     */
    private int fillColumn(int column, ArrayList<NonogramImage.Segment> sequence) {
        int filledNew = 0;

        boolean isColored[][] = new boolean[image.getColors() + 1][image.getHeight()];
        boolean isWhite[] = new boolean[image.getHeight()];
        for (int row = 0; row < image.getHeight(); row++) {
            isWhite[row] = field[row][column] == EXACTLY_WHITE;
            for (int color = 1; color <= image.getColors(); color++) {
                isColored[color][row] = field[row][column] == color;
                isColored[0][row] |= isColored[color][row];
            }
        }

        int updatedColors[] = getUpdatedColors(sequence, isColored, isWhite);
        for (int row = 0; row < image.getHeight(); row++) {
            filledNew += updatedColors[row] != field[row][column] ? 1 : 0;
            field[row][column] = updatedColors[row];
        }

        return filledNew;
    }

    /**
     * Tries to fill nonogram's row or column.
     * It is used in {@code fillRow} and {@code fillColumn} functions to
     * get rid of the separation of nonogram's rows and columns.
     *
     * @param sequence  sequence of segments' sizes and colors
     * @param isColored array that for each cell stores if it is exactly painted
     *                  some color or not
     * @param isWhite   array that for each cell stores if it is exactly white or not
     * @return array with updated colors of row/column
     */
    private int[] getUpdatedColors(ArrayList<NonogramImage.Segment> sequence,
                                   boolean[][] isColored, boolean[] isWhite) {
        int length = isWhite.length;

        boolean canFitPrefix[][] = getPossiblePrefixes(sequence, isColored, isWhite);
        Collections.reverse(sequence);

        boolean canFitSuffix[][] = getPossiblePrefixes(sequence,
                reverseArray(isColored), reverseArray(isWhite));
        Collections.reverse(sequence);

        boolean canBeWhite[] = countIfCanBeWhite(sequence, isColored, isWhite,
                canFitPrefix, canFitSuffix);
        int canBeColored[][] = countIfCanBeColored(sequence, isColored, isWhite,
                canFitPrefix, canFitSuffix);

        int updatedColors[] = new int[length];
        for (int i = 0; i < length; i++) {
            if (canBeColored[0][i] > 0 && !canBeWhite[i]) {
                updatedColors[i] = canBeColored[0][i];
            }
            if (canBeColored[0][i] == 0 && canBeWhite[i]) {
                updatedColors[i] = EXACTLY_WHITE;
            }
        }

        return updatedColors;
    }

    /**
     * For each cell of row/column counts if it can stay white or not.
     *
     * @param sequence     sequence of segments' sizes and colors
     * @param isColored    array that for each cell stores if it is exactly painted some color or not
     * @param isWhite      array that for each cell stores if it is exactly white or not
     * @param canFitPrefix array that for each prefix and each number of blocks stores if this
     *                     prefix can be covered with this number of blocks or not
     * @param canFitSuffix array that for each suffix and each number of blocks stores if this
     *                     prefix can be covered with this number of blocks or not
     * @return array that for each cell stores if it can stay white or not.
     */
    private boolean[] countIfCanBeWhite(ArrayList<NonogramImage.Segment> sequence,
                                        boolean[][] isColored, boolean[] isWhite,
                                        boolean[][] canFitPrefix, boolean[][] canFitSuffix) {
        int length = isWhite.length;
        int blocks = sequence.size();

        boolean canBeWhite[] = new boolean[length];
        for (int i = 0; i < length; i++) {
            for (int k = 0; k <= blocks; k++) {
                if (!isColored[0][i] && canFitPrefix[k][i] && canFitSuffix[blocks - k][length - i - 1]) {
                    canBeWhite[i] = true;
                }
            }
        }
        return canBeWhite;
    }

    /**
     * For each cell of row/column counts if it can be painted some color or not.
     * Returns array of {@code colors + 1} rows. Each row between {@code 1} and {@code colors}
     * corresponds to some color and stores for each cell if it can be painted this color or not.
     * The zero row of the array stores for each cell the type of color it can be painted in if there
     * is exactly one such color, 0 if it can not be painted any color, and -1 otherwise.
     *
     * @param sequence     sequence of segments' sizes and colors
     * @param isColored    array that for each cell stores if it is exactly painted some color or not
     * @param isWhite      array that for each cell stores if it is exactly white or not
     * @param canFitPrefix array that for each prefix and each number of blocks stores if this
     *                     prefix can be covered with this number of blocks or not
     * @param canFitSuffix array that for each suffix and each number of blocks stores if this
     *                     prefix can be covered with this number of blocks or not
     * @return array that for each cell stores if it can be painted some color or not
     */
    private int[][] countIfCanBeColored(ArrayList<NonogramImage.Segment> sequence,
                                        boolean[][] isColored, boolean[] isWhite,
                                        boolean[][] canFitPrefix, boolean[][] canFitSuffix) {
        int length = isWhite.length;
        int blocks = sequence.size();
        int colors = isColored.length;

        int countWhite[] = getPrefixSums(isWhite);
        int countColored[][] = new int[colors][];
        for (int color = 0; color < colors; color++) {
            countColored[color] = getPrefixSums(isColored[color]);
        }

        int intervals[][] = new int[colors][length + 1];
        for (int k = 0; k < blocks; k++) {
            int size = sequence.get(k).getSize();
            int color = sequence.get(k).getColorType();

            for (int i = 0; i + size <= length; i++) {
                if (i > 0 && isColored[color][i - 1]) {
                    continue;
                }
                if (i + size < length && isColored[color][i + size]) {
                    continue;
                }
                if (countWhite[i + size] - countWhite[i] != 0) {
                    continue;
                }
                if (countColored[0][i + size] - countColored[0][i] !=
                        countColored[color][i + size] - countColored[color][i]) {
                    continue;
                }

                int equalsPrev = k > 0 && sequence.get(k - 1).getColorType() == color ? 1 : 0;
                int equalsNext = k + 1 < blocks && sequence.get(k + 1).getColorType() == color ? 1 : 0;
                if (canFitPrefix[k][Math.max(0, i - equalsPrev)] &&
                        canFitSuffix[blocks - k - 1][Math.max(0, length - i - size - equalsNext)]) {
                    intervals[color][i]++;
                    intervals[color][i + size]--;
                }
            }

        }

        int canBeColored[][] = new int[colors][length];
        for (int color = 1; color < colors; color++) {
            for (int i = 0, inside = 0; i < length; i++) {
                inside += intervals[color][i];
                if (inside > 0) {
                    canBeColored[color][i] = 1;
                    canBeColored[0][i] = canBeColored[0][i] == 0 ? color : -1;
                }
            }
        }

        return canBeColored;
    }

    /**
     * For each size of prefix and each number of blocks counts if it is possible to cover
     * this prefix with this number of blocks correctly.
     * @param sequence  sequence of segments' sizes and colors
     * @param isColored array that for each cell stores if it is exactly painted
     *                  some color or not
     * @param isWhite   array that for each cell stores if it is exactly white or not
     * @return array that for each state (pair of prefix size and number of blocks)
     * stores if it is correct or not
     */
    private boolean[][] getPossiblePrefixes(ArrayList<NonogramImage.Segment> sequence,
                                            boolean[][] isColored, boolean[] isWhite) {
        int length = isWhite.length;
        int blocks = sequence.size();

        int countWhite[] = getPrefixSums(isWhite);
        int countColored[][] = new int[isColored.length][];
        for (int color = 0; color < isColored.length; color++) {
            countColored[color] = getPrefixSums(isColored[color]);
        }

        boolean possible[][] = new boolean[blocks + 1][length + 1];
        for (int i = 0; i <= length; i++) {
            possible[0][i] = i == 0 || possible[0][i - 1] && !isColored[0][i - 1];
        }

        for (int k = 1; k <= blocks; k++) {
            int size = sequence.get(k - 1).getSize();
            int color = sequence.get(k - 1).getColorType();

            for (int i = 1; i <= length; i++) {
                if (!isColored[0][i - 1]) {
                    possible[k][i] = possible[k][i - 1];
                }
                if (!isWhite[i - 1]) {
                    if (i < size) {
                        continue;
                    }
                    if (countWhite[i] - countWhite[i - size] != 0) {
                        continue;
                    }
                    if (countColored[0][i] - countColored[0][i - size] !=
                            countColored[color][i] - countColored[color][i - size]) {
                        continue;
                    }
                    if (i > size && isColored[color][i - size - 1]) {
                        continue;
                    }

                    int equalsPrev = k > 1 && sequence.get(k - 2).getColorType() == color ? 1 : 0;
                    possible[k][i] |= possible[k - 1][Math.max(0, i - size - equalsPrev)];
                }
            }
        }

        return possible;
    }

    /**
     * Counts prefix sums for boolean array.
     * Actually, this function returns an array that for each prefix stores
     * the number of {@code true} elements on the prefix of specified array.
     * @param array specified array
     * @return array of prefix sums
     */
    private int[] getPrefixSums(boolean[] array) {
        int length = array.length;
        int sums[] = new int[length + 1];
        for (int i = 1; i <= length; i++) {
            sums[i] = sums[i - 1] + (array[i - 1] ? 1 : 0);
        }
        return sums;
    }

    /**
     * Reverses boolean array.
     * @param array specified array
     * @return reversed array
     */
    private boolean[] reverseArray(boolean[] array) {
        int columns = array.length;
        boolean copy[] = new boolean[columns];
        for (int i = 0; i < columns; i++) {
            copy[i] = array[columns - i - 1];
            copy[columns - i - 1] = array[i];
        }
        return copy;
    }

    /**
     * Reverses each row of boolean two-dimensional array.
     * @param array specified array
     * @return reversed array
     */
    private boolean[][] reverseArray(boolean[][] array) {
        int rows = array.length;
        boolean copy[][] = new boolean[rows][];
        for (int i = 0; i < rows; i++) {
            copy[i] = reverseArray(array[i]);
        }
        return copy;
    }
}

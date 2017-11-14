package ru.spbau.nonograms.logic;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Solver for Japanese crosswords.
 * Temporarily supports solving black-n-white crosswords only.
 */
class BlackAndWhiteNonogramSolver {
    private NonogramImage image;
    private int field[][];

    private boolean isSolved = false;
    private boolean isCorrect = false;

    BlackAndWhiteNonogramSolver(NonogramImage image) {
        this.image = image;
        this.field = new int[image.getHeight()][image.getWidth()];
    }

    void print() {
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (field[i][j] == -1) {
                    System.err.print(" ");
                }
                if (field[i][j] == 0) {
                    System.err.print("?");
                }
                if (field[i][j] > 0) {
                    System.err.print("#");
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
            for (int col = 0; col < image.getWidth(); col++) {
                int added = fillColumn(col, image.getColumn(col));
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
     * @param row nonogram's row
     * @param sequence sequence of segments' sizes and colors
     * @return number of cells that were marked on this step
     */
    private int fillRow(int row, ArrayList<NonogramImage.Segment> sequence) {
        int filledNew = 0;

        boolean isBlack[] = new boolean[image.getWidth()];
        boolean isWhite[] = new boolean[image.getWidth()];
        for (int col = 0; col < image.getWidth(); col++) {
            isBlack[col] = field[row][col] == 1;
            isWhite[col] = field[row][col] == -1;
        }

        int colors[] = getUpdatedColors(sequence, isBlack, isWhite);
        for (int col = 0; col < image.getWidth(); col++) {
            filledNew += colors[col] != field[row][col] ? 1 : 0;
            field[row][col] = colors[col];
        }

        return filledNew;
    }

    /**
     * Tries to fill a column. Marks each cell depending on it's type:
     * "filled", "can be filled" or "can not be filled".
     * @param col nonogram's column
     * @param sequence sequence of segments' sizes and colors
     * @return number of cells that were marked on this step
     */
    private int fillColumn(int col, ArrayList<NonogramImage.Segment> sequence) {
        int filledNew = 0;

        boolean isBlack[] = new boolean[image.getHeight()];
        boolean isWhite[] = new boolean[image.getHeight()];
        for (int row = 0; row < image.getHeight(); row++) {
            isBlack[row] = field[row][col] == 1;
            isWhite[row] = field[row][col] == -1;
        }

        int colors[] = getUpdatedColors(sequence, isBlack, isWhite);
        for (int row = 0; row < image.getHeight(); row++) {
            filledNew += colors[row] != field[row][col] ? 1 : 0;
            field[row][col] = colors[row];
        }

        return filledNew;
    }

    /**
     * Tries to fill nonogram's row or column.
     * It is used in {@code fillRow} and {@code fillColumn} functions to
     * get rid of the separation of nonogram's rows and columns.
     *
     *
     * @param sequence sequence of segments' sizes and colors
     * @param isBlack array that for each cell stores if it is exactly black or not
     * @param isWhite array that for each cell stores if it is exactly white or not
     * @return array with updated colors of row/column
     */
    private int[] getUpdatedColors(ArrayList<NonogramImage.Segment> sequence,
                                   boolean[] isBlack, boolean[] isWhite) {
        int length = isBlack.length;
        int blocks = sequence.size();

        boolean canFitPrefix[][] = getPossiblePrefixes(sequence, isBlack, isWhite);
        Collections.reverse(sequence);

        boolean canFitSuffix[][] = getPossiblePrefixes(sequence,
                reverseArray(isBlack), reverseArray(isWhite));
        Collections.reverse(sequence);

        boolean canBeWhite[] = new boolean[length];
        for (int i = 0; i < length; i++) {
            for (int k = 0; k <= blocks; k++) {
                if (!isBlack[i] && canFitPrefix[k][i] && canFitSuffix[blocks - k][length - i - 1]) {
                    canBeWhite[i] = true;
                }
            }
        }

        int countWhite[] = getPrefixSums(isWhite);

        int intervals[] = new int[length + 1];
        for (int k = 0; k < blocks; k++) {
            NonogramImage.Segment seg = sequence.get(k);
            for (int i = 0; i + seg.getSize() <= length; i++) {
                if (i > 0 && isBlack[i - 1]) {
                    continue;
                }
                if (i + seg.getSize() < length && isBlack[i + seg.getSize()]) {
                    continue;
                }
                if (countWhite[i + seg.getSize()] - countWhite[i] != 0) {
                    continue;
                }
                if (canFitPrefix[k][Math.max(0, i - 1)] &&
                        canFitSuffix[blocks - k - 1][Math.max(0, length - i - seg.getSize() - 1)]) {
                    intervals[i]++;
                    intervals[i + seg.getSize()]--;
                }
            }
        }

        boolean canBeBlack[] = new boolean[length];
        for (int i = 0, inside = 0; i < length; i++) {
            inside += intervals[i];
            canBeBlack[i] = inside > 0;
        }

        int colors[] = new int[length];
        for (int i = 0; i < length; i++) {
            if (canBeBlack[i] && !canBeWhite[i]) {
                colors[i] = 1;
            }
            if (!canBeBlack[i] && canBeWhite[i]) {
                colors[i] = -1;
            }
        }

        return colors;
    }

    /**
     * For each size of prefix and each number of blocks counts if it is possible to cover
     * this prefix with this number of blocks correctly.
     * @param sequence sequence of segments' sizes and colors
     * @param isBlack array that for each cell stores if it is exactly black or not
     * @param isWhite array that for each cell stores if it is exactly white or not
     * @return array that for each state (pair of prefix size and number of blocks)
     * stores if it is correct or not
     */
    private boolean[][] getPossiblePrefixes(ArrayList<NonogramImage.Segment> sequence,
                                            boolean[] isBlack, boolean[] isWhite) {
        int length = isBlack.length;
        int blocks = sequence.size();

        int countWhite[] = getPrefixSums(isWhite);

        boolean possible[][] = new boolean[blocks + 1][length + 1];
        for (int i = 0; i <= length; i++) {
            possible[0][i] = i == 0 || possible[0][i - 1] && !isBlack[i - 1];
        }

        for (int k = 1; k <= blocks; k++) {
            NonogramImage.Segment seg = sequence.get(k - 1);
            for (int i = 1; i <= length; i++) {
                if (!isBlack[i - 1]) {
                    possible[k][i] = possible[k][i - 1];
                }
                if (!isWhite[i - 1]) {
                    if (i < seg.getSize()) {
                        continue;
                    }
                    if (countWhite[i] - countWhite[i - seg.getSize()] != 0) {
                        continue;
                    }
                    if (i > seg.getSize() && isBlack[i - seg.getSize() - 1]) {
                        continue;
                    }
                    possible[k][i] |= possible[k - 1][Math.max(0, i - seg.getSize() - 1)];
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
        int length = array.length;
        boolean copy[] = array.clone();
        for (int i = 0; i < length; i++) {
            copy[i] = array[length - i - 1];
            copy[length - i - 1] = array[i];
        }
        return copy;
    }
}

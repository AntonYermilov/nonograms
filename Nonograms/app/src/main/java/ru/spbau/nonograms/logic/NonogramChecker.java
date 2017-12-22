package ru.spbau.nonograms.logic;

import ru.spbau.nonograms.local_database.CurrentCrosswordState;

/**
 * Checks whether nonogram is solved correctly or not.
 */
class NonogramChecker {

    /**
     * Checks if current nonogram's state satisfies the correct solution.
     * @param state specified nonogram's state
     * @return {@code true} if nonogram is solved correctly; {@code false} otherwise
     */
    static boolean check(CurrentCrosswordState state) {
        int height = state.getHeight();
        int width = state.getWidth();

        for (int y = 0; y < height; y++) {
            int seg = 0;
            CurrentCrosswordState.ColoredValue[] row = state.getRows()[y];
            for (int xl = 0, xr = 0; xr < width; xl = xr) {
                while (xr < width && state.getField(xl, y).getColor() == state.getField(xr, y).getColor()) {
                    xr++;
                }
                if (state.getField(xl, y).getValue() != CurrentCrosswordState.FILLED_CELL) {
                    continue;
                }
                if (seg == row.length || state.getField(xl, y).getColor() != row[seg].getColor()
                        || xr - xl != row[seg].getValue()) {
                    return false;
                }
                seg++;
            }
            if (seg != row.length) {
                return false;
            }
        }

        for (int x = 0; x < width; x++) {
            int seg = 0;
            CurrentCrosswordState.ColoredValue[] column = state.getColumns()[x];
            for (int yl = 0, yr = 0; yr < height; yl = yr) {
                while (yr < height && state.getField(x, yl).getColor() == state.getField(x, yr).getColor()) {
                    yr++;
                }
                if (state.getField(x, yl).getValue() != CurrentCrosswordState.FILLED_CELL) {
                    continue;
                }
                if (seg == column.length || state.getField(x, yl).getColor() != column[seg].getColor()
                        || yr - yl != column[seg].getValue()) {
                    return false;
                }
                seg++;
            }
            if (seg != column.length) {
                return false;
            }
        }

        return true;
    }

}

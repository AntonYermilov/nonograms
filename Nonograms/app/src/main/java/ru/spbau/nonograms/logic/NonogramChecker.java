package ru.spbau.nonograms.logic;

import java.util.List;

/**
 * Checks whether nonogram is solved correctly or not.
 */
class NonogramChecker {

    /**
     * Receives field of RGB colors.
     * Checks is it satisfies specified nonogram.
     * @param field field of RGB colors
     * @param image specified nonogram image
     * @return {@code true} if field satisfies specified nonogram; {@code false} otherwise
     */
    static boolean check(int[][] field, NonogramImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        for (int i = 0; i < height; i++) {
            List<NonogramImage.Segment> row = image.getRow(i);
            int seg = 0;

            for (int l = 0, r = 0; r < width; l = r) {
                while (r < width && field[i][l] == field[i][r]) {
                    r++;
                }
                if (field[i][l] != image.getBackgroundColor()) {
                    if (seg == row.size() || field[i][l] != row.get(seg).getRGBColor()) {
                        return false;
                    }
                    seg++;
                }
            }
            if (seg != row.size()) {
                return false;
            }
        }

        for (int i = 0; i < width; i++) {
            List<NonogramImage.Segment> column = image.getColumn(i);
            int seg = 0;

            for (int l = 0, r = 0; r < height; l = r) {
                while (r < height && field[l][i] == field[r][i]) {
                    r++;
                }
                if (field[l][i] != image.getBackgroundColor()) {
                    if (seg == column.size() || field[l][i] != column.get(seg).getRGBColor()) {
                        return false;
                    }
                    seg++;
                }
            }
            if (seg != column.size()) {
                return false;
            }
        }

        return true;
    }

}

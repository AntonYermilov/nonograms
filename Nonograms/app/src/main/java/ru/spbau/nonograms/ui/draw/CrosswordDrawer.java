package ru.spbau.nonograms.ui.draw;

import android.graphics.Color;
import android.graphics.Paint;

public class CrosswordDrawer {

    public static final int OFFSET_X = 50;
    public static final int OFFSET_Y = 50;

    public static final Paint LINE_PAINT;
    public static final int CELL_SIZE = 75;

    static {
        LINE_PAINT = new Paint();
        LINE_PAINT.setColor(Color.BLACK);
        LINE_PAINT.setStrokeWidth(5);
    }

    public static void drawBackground(CrosswordCanvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i <= 5 * CELL_SIZE; i += CELL_SIZE) {
            canvas.drawLine(0, i, 5 * CELL_SIZE, i, LINE_PAINT);
        }
        for (int i = 0; i <= 5 * CELL_SIZE; i += CELL_SIZE) {
            canvas.drawLine(i, 0, i, 5 * CELL_SIZE, LINE_PAINT);
        }
    }

    public static void drawTable(CrosswordCanvas canvas, int table[][]) {
        drawBackground(canvas);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (table[i][j] == 1) {
                    canvas.drawCross(i * CELL_SIZE, j * CELL_SIZE,
                            (i + 1) * CELL_SIZE, (j + 1) * CELL_SIZE, LINE_PAINT);
                } else if (table[i][j] == 2) {
                    canvas.drawSquare(i * CELL_SIZE, j * CELL_SIZE,
                            (i + 1) * CELL_SIZE, (j + 1) * CELL_SIZE, LINE_PAINT);
                }
            }
        }
    }
}

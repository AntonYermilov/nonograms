package ru.spbau.nonograms.ui.draw;

import android.graphics.Color;
import android.graphics.Paint;

import ru.spbau.nonograms.local_database.CurrentCrosswordState;

public class CrosswordDrawer {

    private static int offsetX = 50;
    private static int offsetY = 50;

    private static int aboveHeaderSize;
    private static int leftHeaderSize;

    public static final Paint LINE_PAINT;
    public static final int CELL_SIZE = 90;

    static {
        LINE_PAINT = new Paint();
        LINE_PAINT.setColor(Color.BLACK);
        LINE_PAINT.setStrokeWidth(5);
    }

    private static void drawNumbers(CrosswordCanvas canvas, CurrentCrosswordState table) {
        LINE_PAINT.setTextSize(70);
        LINE_PAINT.setTextAlign(Paint.Align.CENTER);

        CurrentCrosswordState.ColoredValue[][] columns = table.getColumns();

        for (int i = 0; i < columns.length; i++) {
            for (int j = 0; j < columns[i].length; j++) {
                float[] xy = countCentreOfTheNumber(columns[i][j].getValue() + "", 0, 0, CELL_SIZE, CELL_SIZE, LINE_PAINT);
                LINE_PAINT.setColor(columns[i][j].getColor());
                canvas.drawNumber(offsetX + leftHeaderSize, offsetY, columns[i][j].getValue(),
                        i * CELL_SIZE + xy[0],
                        (j + table.getColumnsMax() - columns[i].length) * CELL_SIZE + xy[1], LINE_PAINT);
            }
        }

        CurrentCrosswordState.ColoredValue[][] rows = table.getRows();

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                float[] xy = countCentreOfTheNumber(rows[i][j].getValue() + "", 0, 0, CELL_SIZE, CELL_SIZE, LINE_PAINT);
                LINE_PAINT.setColor(rows[i][j].getColor());
                canvas.drawNumber(offsetX, offsetY + aboveHeaderSize, rows[i][j].getValue(),
                        (j + table.getRowsMax() - rows[i].length) * CELL_SIZE + xy[0],
                        i * CELL_SIZE + xy[1], LINE_PAINT);
            }
        }
    }

    public static void drawBackground(CrosswordCanvas canvas, CurrentCrosswordState table) {
        aboveHeaderSize = table.getColumnsMax() * CELL_SIZE;
        leftHeaderSize = table.getRowsMax() * CELL_SIZE;
        canvas.drawColor(Color.WHITE);

        canvas.drawLine(offsetX, offsetY, 0, 0, table.getWidth() * CELL_SIZE + leftHeaderSize, 0, LINE_PAINT);
        canvas.drawLine(offsetX, offsetY, 0, 0, 0, table.getHeight() * CELL_SIZE + aboveHeaderSize, LINE_PAINT);


        LINE_PAINT.setColor(Color.LTGRAY);

        for (int i = CELL_SIZE; i < table.getColumnsMax() * CELL_SIZE; i += CELL_SIZE) {
            canvas.drawLine(offsetX + leftHeaderSize, offsetY,
                    0, i, table.getWidth() * CELL_SIZE, i, LINE_PAINT);
        }

        for (int i = CELL_SIZE; i < table.getRowsMax() * CELL_SIZE; i += CELL_SIZE) {
            canvas.drawLine(offsetX, offsetY + aboveHeaderSize,
                    i, 0, i, table.getHeight() * CELL_SIZE, LINE_PAINT);
        }

        LINE_PAINT.setColor(Color.BLACK);
        LINE_PAINT.setStyle(Paint.Style.FILL);

        drawNumbers(canvas, table);

        LINE_PAINT.setColor(Color.BLACK);
        LINE_PAINT.setStyle(Paint.Style.FILL);

        for (int i = 0; i <= table.getHeight() * CELL_SIZE; i += CELL_SIZE) {
            canvas.drawLine(offsetX, offsetY + aboveHeaderSize,
                    0, i, table.getWidth() * CELL_SIZE + leftHeaderSize, i, LINE_PAINT);
        }
        for (int i = 0; i <= table.getWidth() * CELL_SIZE; i += CELL_SIZE) {
            canvas.drawLine(offsetX + leftHeaderSize, offsetY,
                    i, 0, i, table.getHeight() * CELL_SIZE + aboveHeaderSize, LINE_PAINT);
        }
    }

    public static void drawTable(CrosswordCanvas canvas, CurrentCrosswordState table) {
        drawBackground(canvas, table);
        for (int i = 0; i < table.getWidth(); i++) {
            for (int j = 0; j < table.getHeight(); j++) {
                LINE_PAINT.setColor(table.getField(i, j).getColor());
                if (table.getField(i, j).getValue() == 1) {
                    canvas.drawCross(offsetX + leftHeaderSize, offsetY + aboveHeaderSize,
                            i * CELL_SIZE, j * CELL_SIZE,
                            (i + 1) * CELL_SIZE, (j + 1) * CELL_SIZE, LINE_PAINT);
                } else if (table.getField(i, j).getValue() == 2) {
                    canvas.drawSquare(offsetX + leftHeaderSize, offsetY + aboveHeaderSize,
                            i * CELL_SIZE, j * CELL_SIZE,
                            (i + 1) * CELL_SIZE, (j + 1) * CELL_SIZE, LINE_PAINT);
                }
                LINE_PAINT.setColor(Color.BLACK);
            }
        }
    }

    private static float[] countCentreOfTheNumber(String text, int x1, int y1, int x2, int y2, Paint textPaint) {
        float xPos = (x2 - x1) / 2;
        float yPos = ((y2 - y1) / 2 - ((textPaint.descent() + textPaint.ascent()) / 2));
        return new float[] {xPos, yPos};
    }

    public static int getSumOffsetX() {
        return offsetX + leftHeaderSize;
    }

    public static int getSumOffsetY() {
        return offsetY + aboveHeaderSize;
    }
}

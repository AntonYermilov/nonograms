package ru.spbau.nonograms.ui.draw;

import android.graphics.Color;
import android.graphics.Paint;

import ru.spbau.nonograms.local_database.CurrentCrosswordState;

public class CrosswordDrawer {

    private static int offsetX = 50;
    private static int offsetY = 50;

    private static int aboveHeaderSize;
    private static int leftHeaderSize;

    private static final Paint LINE_PAINT;
    private static int cellSize = 90;
    private static int fontSize = 70;

    static {
        LINE_PAINT = new Paint();
        LINE_PAINT.setColor(Color.BLACK);
        LINE_PAINT.setStrokeWidth(5);
    }

    public static void staticInit() {
        offsetX = 50;
        offsetY = 50;
        cellSize = 90;
        fontSize = 70;
    }

    private static void drawNumbers(CrosswordCanvas canvas, CurrentCrosswordState table) {
        LINE_PAINT.setTextSize(fontSize);
        LINE_PAINT.setTextAlign(Paint.Align.CENTER);

        CurrentCrosswordState.ColoredValue[][] columns = table.getColumns();

        for (int i = 0; i < columns.length; i++) {
            for (int j = 0; j < columns[i].length; j++) {
                float[] xy = countCentreOfTheNumber(columns[i][j].getValue() + "", 0, 0, cellSize, cellSize, LINE_PAINT);
                LINE_PAINT.setColor(columns[i][j].getColor());
                canvas.drawNumber(offsetX + leftHeaderSize, offsetY, columns[i][j].getValue(),
                        i * cellSize + xy[0],
                        (j + table.getColumnsMax() - columns[i].length) * cellSize + xy[1], LINE_PAINT);
            }
        }

        CurrentCrosswordState.ColoredValue[][] rows = table.getRows();

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                float[] xy = countCentreOfTheNumber(rows[i][j].getValue() + "", 0, 0, cellSize, cellSize, LINE_PAINT);
                LINE_PAINT.setColor(rows[i][j].getColor());
                canvas.drawNumber(offsetX, offsetY + aboveHeaderSize, rows[i][j].getValue(),
                        (j + table.getRowsMax() - rows[i].length) * cellSize + xy[0],
                        i * cellSize + xy[1], LINE_PAINT);
            }
        }
    }

    public static void drawBackground(CrosswordCanvas canvas, CurrentCrosswordState table) {
        aboveHeaderSize = table.getColumnsMax() * cellSize;
        leftHeaderSize = table.getRowsMax() * cellSize;
        canvas.drawColor(Color.WHITE);

        canvas.drawLine(offsetX, offsetY, 0, 0, table.getWidth() * cellSize + leftHeaderSize, 0, LINE_PAINT);
        canvas.drawLine(offsetX, offsetY, 0, 0, 0, table.getHeight() * cellSize + aboveHeaderSize, LINE_PAINT);


        LINE_PAINT.setColor(Color.LTGRAY);

        for (int i = cellSize; i < table.getColumnsMax() * cellSize; i += cellSize) {
            canvas.drawLine(offsetX + leftHeaderSize, offsetY,
                    0, i, table.getWidth() * cellSize, i, LINE_PAINT);
        }

        for (int i = cellSize; i < table.getRowsMax() * cellSize; i += cellSize) {
            canvas.drawLine(offsetX, offsetY + aboveHeaderSize,
                    i, 0, i, table.getHeight() * cellSize, LINE_PAINT);
        }

        LINE_PAINT.setColor(Color.BLACK);
        LINE_PAINT.setStyle(Paint.Style.FILL);

        drawNumbers(canvas, table);

        LINE_PAINT.setColor(Color.BLACK);
        LINE_PAINT.setStyle(Paint.Style.FILL);

        for (int i = 0; i <= table.getHeight() * cellSize; i += cellSize) {
            canvas.drawLine(offsetX, offsetY + aboveHeaderSize,
                    0, i, table.getWidth() * cellSize + leftHeaderSize, i, LINE_PAINT);
        }
        for (int i = 0; i <= table.getWidth() * cellSize; i += cellSize) {
            canvas.drawLine(offsetX + leftHeaderSize, offsetY,
                    i, 0, i, table.getHeight() * cellSize + aboveHeaderSize, LINE_PAINT);
        }
    }

    public static void drawTable(CrosswordCanvas canvas, CurrentCrosswordState table) {
        drawBackground(canvas, table);
        for (int i = 0; i < table.getWidth(); i++) {
            for (int j = 0; j < table.getHeight(); j++) {
                if (table.getField(i, j).getValue() == 1) {
                    canvas.drawCross(offsetX + leftHeaderSize, offsetY + aboveHeaderSize,
                            i * cellSize, j * cellSize,
                            (i + 1) * cellSize, (j + 1) * cellSize, LINE_PAINT);
                } else if (table.getField(i, j).getValue() == 2) {
                    LINE_PAINT.setColor(table.getField(i, j).getColor());
                    canvas.drawSquare(offsetX + leftHeaderSize, offsetY + aboveHeaderSize,
                            i * cellSize, j * cellSize,
                            (i + 1) * cellSize, (j + 1) * cellSize, LINE_PAINT);
                    LINE_PAINT.setColor(Color.BLACK);
                }
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

    public static void addToOffsetX(int add) {
        offsetX += add;
    }

    public static void addToOffsetY(int add) {
        offsetY += add;
    }

    public static void recountScale(double scaleFactor) {
        cellSize = (int)Math.round(90 * scaleFactor);
        fontSize = (int)Math.round(70 * scaleFactor);
    }

    public static double getCellSize() {
        return cellSize;
    }
}

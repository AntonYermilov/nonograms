package ru.spbau.nonograms.ui.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CrosswordCanvas {
    private Canvas canvas;

    public CrosswordCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawLine(int offsetX, int offsetY,
                         float x1, float y1, float x2, float y2, Paint paint) {
        canvas.drawLine(x1 + offsetX, y1 + offsetY, x2 + offsetX, y2 + offsetY, paint);
    }

    public void drawCross(int offsetX, int offsetY,
                          float x1, float y1, float x2, float y2, Paint paint) {
        canvas.drawLine(x1 + offsetX, y1 + offsetY, x2 + offsetX, y2 + offsetY, paint);
        canvas.drawLine(x1 + offsetX, y2 + offsetY, x2 + offsetX, y1 + offsetY, paint);
    }

    public void drawSquare(int offsetX, int offsetY,
                           float x1, float y1, float x2, float y2, Paint paint) {
        canvas.drawRect(x1 + offsetX, y1 + offsetY, x2 + offsetX, y2 + offsetY, paint);
    }

    public void drawNumber(int offsetX, int offsetY, int number, float x, float y, Paint paint) {
        canvas.drawText(Integer.toString(number), x + offsetX, y + offsetY, paint);
    }

    public void drawColor(int color) {
        canvas.drawColor(color);
    }

    public float getHeight() {
        return canvas.getHeight();
    }

    public float getWidth() {
        return canvas.getWidth();
    }

}

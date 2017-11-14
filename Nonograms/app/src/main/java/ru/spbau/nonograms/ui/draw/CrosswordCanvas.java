package ru.spbau.nonograms.ui.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CrosswordCanvas {
    private Canvas canvas;

    public CrosswordCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        canvas.drawLine(startX + CrosswordDrawer.OFFSET_X, startY + CrosswordDrawer.OFFSET_Y,
                stopX + CrosswordDrawer.OFFSET_X, stopY + CrosswordDrawer.OFFSET_Y, paint);
    }

    public void drawCross(float x1, float y1, float x2, float y2, Paint paint) {
        canvas.drawLine(x1 + CrosswordDrawer.OFFSET_X, y1 + CrosswordDrawer.OFFSET_Y,
                x2 + CrosswordDrawer.OFFSET_X, y2 + CrosswordDrawer.OFFSET_Y, paint);
        canvas.drawLine(x1 + CrosswordDrawer.OFFSET_X, y2 + CrosswordDrawer.OFFSET_Y,
                x2 + CrosswordDrawer.OFFSET_X, y1 + CrosswordDrawer.OFFSET_Y, paint);
    }

    public void drawSquare(float x1, float y1, float x2, float y2, Paint paint) {
        canvas.drawRect(x1 + CrosswordDrawer.OFFSET_X, y1 + CrosswordDrawer.OFFSET_Y,
                x2 + CrosswordDrawer.OFFSET_X, y2 + CrosswordDrawer.OFFSET_Y, paint);
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

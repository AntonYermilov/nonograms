package ru.spbau.nonograms.ui;

import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.ui.draw.CrosswordCanvas;
import ru.spbau.nonograms.ui.draw.CrosswordDrawer;

public class CrosswordActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {

    private SurfaceHolder surfaceHolder;

    int[][] current = new int[5][5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword);

        final SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceView);

        surfaceHolder = surface.getHolder();
        surfaceHolder.addCallback(this);
        surface.setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        CrosswordCanvas myCanvas = new CrosswordCanvas(canvas);
        CrosswordDrawer.drawBackground(myCanvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        double mX = motionEvent.getX();
        double mY = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int)Math.floor((mX - CrosswordDrawer.OFFSET_X) / CrosswordDrawer.CELL_SIZE);
                int y = (int)Math.floor((mY - CrosswordDrawer.OFFSET_Y) / CrosswordDrawer.CELL_SIZE);
                if (x < 5 && x >= 0 && y < 5 && y >= 0) {
                    current[x][y] += 1;
                    current[x][y] %= 3;
                }
                redraw();
            }

        }
        return true;
    }

    public void redraw() {
        Canvas canvas = surfaceHolder.lockCanvas();
        CrosswordCanvas myCanvas = new CrosswordCanvas(canvas);
        CrosswordDrawer.drawTable(myCanvas, current);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }
}

package ru.spbau.nonograms.ui;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.ui.draw.CrosswordCanvas;
import ru.spbau.nonograms.ui.draw.CrosswordDrawer;

public class CrosswordActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {

    private SurfaceHolder surfaceHolder;

    CurrentCrosswordState current = new CurrentCrosswordState(new int[][] {
            {1, 1},
            {1, 1, 1},
            {1, 1},
            {1, 1},
            {1}
    }, new int[][] {
            {2},
            {1, 1},
            {1, 1},
            {1, 1},
            {2}
    }, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword);

        final SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceView);

        surfaceHolder = surface.getHolder();
        surfaceHolder.addCallback(this);
        surface.setOnTouchListener(this);

        Button checkButton = (Button) findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkResult = Controller.checkCorrectness(current);
                TextView messageResult = new TextView(CrosswordActivity.this);
                messageResult.setGravity(Gravity.CENTER);
                messageResult.setTextSize(20);
                if (checkResult) {
                    messageResult.setText("WOW! You solved it right!");
                } else {
                    messageResult.setText("There are some mistakes, try to solve it again. =(");
                }
                AlertDialog info = new AlertDialog.Builder(CrosswordActivity.this).create();
                info.setView(messageResult);
                info.show();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        CrosswordCanvas myCanvas = new CrosswordCanvas(canvas);
        CrosswordDrawer.drawBackground(myCanvas, current);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        redraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //redraw();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        double mX = motionEvent.getX();
        double mY = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int)Math.floor((mX - CrosswordDrawer.getSumOffsetX()) / CrosswordDrawer.CELL_SIZE);
                int y = (int)Math.floor((mY - CrosswordDrawer.getSumOffsetY()) / CrosswordDrawer.CELL_SIZE);
                if (x < current.getWidth() && x >= 0 && y < current.getHeight() && y >= 0) {
                    current.setField(x, y, (current.getField(x, y) + 1) % 3);
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

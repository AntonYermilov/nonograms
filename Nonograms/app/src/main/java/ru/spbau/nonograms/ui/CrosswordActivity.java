package ru.spbau.nonograms.ui;

import android.graphics.Canvas;
import android.graphics.Color;
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

import java.io.IOException;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.ui.draw.CrosswordCanvas;
import ru.spbau.nonograms.ui.draw.CrosswordDrawer;

public class CrosswordActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {

    private SurfaceHolder surfaceHolder;

    private CurrentCrosswordState current;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword);

        filename = getIntent().getStringExtra("Data");
        try {
            current = Controller.getLocalCrosswordByFilename(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
    protected void onPause() {
        super.onPause();

        try {
            Controller.updateLocalyByFilename(filename, current);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    current.setField(x, y, new CurrentCrosswordState.ColoredValue(
                            (current.getField(x, y).getValue() + 1) % 3, Color.BLACK));
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

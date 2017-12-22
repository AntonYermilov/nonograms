package ru.spbau.nonograms.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.ui.draw.CrosswordCanvas;
import ru.spbau.nonograms.ui.draw.CrosswordDrawer;

public class CrosswordActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnTouchListener {

    private SurfaceHolder surfaceHolder;

    private CurrentCrosswordState current;
    private String filename;
    private int lastColor;
    private GestureDetectorCompat generalDetector;
    private ScaleGestureDetector scaleDetector;
    private double motionStartX;
    private double motionStartY;
    private double scaleFactor = 1;


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
        generalDetector = new GestureDetectorCompat(this, new GestureDetector());
        scaleDetector = new ScaleGestureDetector(this, new GestureDetector());
        surfaceHolder = surface.getHolder();
        surfaceHolder.addCallback(this);
        surface.setOnTouchListener(this);

        CrosswordDrawer.staticInit();

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

        final int[] colors = current.getColors();
        lastColor = Color.BLACK;
        if (colors.length > 1) {
            LinearLayout colorField = (LinearLayout) findViewById(R.id.ColorField);
            Button[] colorButtons = new Button[colors.length];
            lastColor = colors[0];
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x / 10 * 7;
            for (int i = 0; i < colors.length; i++) {
                colorButtons[i] = new Button(this);
                colorButtons[i].setBackgroundColor(colors[i]);
                colorButtons[i].setMinimumWidth(0);
                colorButtons[i].setWidth(width / (colors.length));
                colorButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lastColor = ((ColorDrawable)(((Button) view).getBackground())).getColor();
                    }
                });
                Log.i("Crossword activity: ", colorButtons[i].getWidth() + " " + width);
                colorField.addView(colorButtons[i]);
            }
        }
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
        scaleDetector.onTouchEvent(motionEvent);
        generalDetector.onTouchEvent(motionEvent);
        return true;
    }

    public void redraw() {
        Canvas canvas = surfaceHolder.lockCanvas();
        CrosswordCanvas myCanvas = new CrosswordCanvas(canvas);
        CrosswordDrawer.drawTable(myCanvas, current);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener
            implements ScaleGestureDetector.OnScaleGestureListener {

        boolean scaling = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("CrosswordActivity: ", "Scaling...");
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.round(scaleFactor * 100) / 100.0; // jitter
            scaleFactor = Math.max(0.1, Math.min(2, scaleFactor));
            CrosswordDrawer.recountScale(scaleFactor);
            redraw();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            scaling = true;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            scaling = false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            double mX = motionEvent.getX();
            double mY = motionEvent.getY();
            int x = (int)Math.floor((mX - CrosswordDrawer.getSumOffsetX()) / CrosswordDrawer.getCellSize());
            int y = (int)Math.floor((mY - CrosswordDrawer.getSumOffsetY()) / CrosswordDrawer.getCellSize());
            if (x < current.getWidth() && x >= 0 && y < current.getHeight() && y >= 0) {
                current.setField(x, y, new CurrentCrosswordState.ColoredValue(
                        (current.getField(x, y).getValue() + 1) % 3, lastColor));
            }
            redraw();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
            if (scaling) {
                return false;
            }
            CrosswordDrawer.addToOffsetX((int)Math.round(-distanceX));
            CrosswordDrawer.addToOffsetY((int)Math.round(-distanceY));
            redraw();
            return true;
        }

    }


}

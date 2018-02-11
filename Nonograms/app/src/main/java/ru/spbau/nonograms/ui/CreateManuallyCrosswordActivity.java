package ru.spbau.nonograms.ui;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.ui.draw.CrosswordCanvas;
import ru.spbau.nonograms.ui.draw.CrosswordDrawer;

public class CreateManuallyCrosswordActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnTouchListener {

    private SurfaceHolder surfaceHolder;

    private CurrentCrosswordState current;
    private int lastColor;
    private GestureDetectorCompat generalDetector;
    private ScaleGestureDetector scaleDetector;
    private double scaleFactor = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_manually_crossword);

        int width = getIntent().getIntExtra("width", 0);
        int height = getIntent().getIntExtra("height", 0);


        CurrentCrosswordState.ColoredValue[][] columns = new CurrentCrosswordState.ColoredValue[width][0];
        CurrentCrosswordState.ColoredValue[][] rows = new CurrentCrosswordState.ColoredValue[height][0];


        current = new CurrentCrosswordState(rows, columns, new int[]{}, Color.WHITE, null);

        final SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceViewCreate);
        generalDetector = new GestureDetectorCompat(this, new GestureDetector());
        scaleDetector = new ScaleGestureDetector(this, new GestureDetector());
        surfaceHolder = surface.getHolder();
        surfaceHolder.addCallback(this);
        surface.setOnTouchListener(this);

        CrosswordDrawer.staticInit();

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputName = new EditText(CreateManuallyCrosswordActivity.this);
                final TextView typeInText = new TextView(CreateManuallyCrosswordActivity.this);
                typeInText.setText(R.string.type_in_name);
                final CheckBox saveOnServer = new CheckBox(CreateManuallyCrosswordActivity.this);
                saveOnServer.setText(R.string.save_on_server);
                LinearLayout dataView = new LinearLayout(CreateManuallyCrosswordActivity.this);
                dataView.setOrientation(LinearLayout.VERTICAL);
                dataView.addView(typeInText);
                dataView.addView(inputName);
                dataView.addView(saveOnServer);
                final AlertDialog dialog = new AlertDialog.Builder(CreateManuallyCrosswordActivity.this)
                        .setTitle("Save your crossword")
                        .setView(dataView)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    Controller.addCrosswordLocallyByField(current, inputName.getText().toString());
                                    if (saveOnServer.isChecked()) {
                                        Controller.addCrosswordOnServerByField(current, inputName.getText().toString());
                                    }
                                } catch (IOException e) {
                                    Log.e("create manually: ", "error saving crossword");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        Button clearButton = (Button) findViewById(R.id.clearButtonCreate);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current.clearField();
                redraw();
            }
        });

        lastColor = Color.BLACK;

    }

    @Override
    protected void onPause() {
        super.onPause();

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


        private boolean scaling = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
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
                        (current.getField(x, y).getValue() + 2) % 3, lastColor));
            }
            redraw();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
            if (scaling) {
                return false;
            }
            CrosswordDrawer.addToOffsetX(Math.round(-distanceX));
            CrosswordDrawer.addToOffsetY(Math.round(-distanceY));
            redraw();
            return true;
        }
    }
}

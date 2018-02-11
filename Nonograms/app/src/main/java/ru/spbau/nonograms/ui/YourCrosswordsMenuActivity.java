package ru.spbau.nonograms.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.spbau.nonograms.R;

public class YourCrosswordsMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_crosswords_menu);

        Button createByPicture = (Button)findViewById(R.id.createByPictureButton);
        createByPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YourCrosswordsMenuActivity.this, CreateByPictureCrosswordActivity.class);
                startActivity(intent);
            }
        });

        Button createManually = (Button) findViewById(R.id.createManuallyButton);
        createManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputX = new EditText(YourCrosswordsMenuActivity.this);
                inputX.setInputType(InputType.TYPE_CLASS_NUMBER);
                final EditText inputY = new EditText(YourCrosswordsMenuActivity.this);
                inputY.setInputType(InputType.TYPE_CLASS_NUMBER);
                TextView multiplySign = new TextView(YourCrosswordsMenuActivity.this);
                multiplySign.setText("x");
                LinearLayout dataView = new LinearLayout(YourCrosswordsMenuActivity.this);
                dataView.setOrientation(LinearLayout.HORIZONTAL);
                dataView.addView(inputX);
                dataView.addView(multiplySign);
                dataView.addView(inputY);
                final AlertDialog dialog = new AlertDialog.Builder(YourCrosswordsMenuActivity.this)
                        .setTitle("Make new crossword")
                        .setMessage("Type in width and height:")
                        .setView(dataView)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(YourCrosswordsMenuActivity.this, CreateManuallyCrosswordActivity.class);
                        String widthString = inputX.getText().toString();
                        String heightString = inputY.getText().toString();
                        if (widthString.length() == 0 || widthString.length() > 3 ||
                                heightString.length() == 0 || heightString.length() > 3) {
                            return;
                        }
                        int width = Integer.parseInt(inputX.getText().toString());
                        int height = Integer.parseInt(inputY.getText().toString());

                        if (width < 5 || width > 80 || height < 5 || height > 80) {
                            return;
                        }
                        intent.putExtra("width", width);
                        intent.putExtra("height", height);
                        dialog.dismiss();
                        startActivity(intent);
                    }
                });
            }
        });
        Button loadNew = (Button) findViewById(R.id.loadNewButton);
        loadNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YourCrosswordsMenuActivity.this, LoadCrosswordsActivity.class);
                startActivity(intent);
            }
        });
    }
}

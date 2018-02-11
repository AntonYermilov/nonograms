package ru.spbau.nonograms.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;

public class CreateByPictureCrosswordActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView givenImage;
    private ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_crossword);

        givenImage = (ImageView)findViewById(R.id.givenImage);
        resultImage = (ImageView)findViewById(R.id.resultImage);

        Button uploadPictureButton = (Button)findViewById(R.id.uploadPictureButton);
        uploadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        Button makePuzzleButton = (Button) findViewById(R.id.makeCrosswordButton);
        makePuzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText inputWidth = new EditText(CreateByPictureCrosswordActivity.this);
                inputWidth.setInputType(InputType.TYPE_CLASS_NUMBER);
                final EditText inputHeight = new EditText(CreateByPictureCrosswordActivity.this);
                inputHeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                TextView multiplySign = new TextView(CreateByPictureCrosswordActivity.this);
                multiplySign.setText("x");
                TextView colorText = new TextView(CreateByPictureCrosswordActivity.this);
                colorText.setText(R.string.NumberOfColorsStr);
                final EditText inputColors = new EditText(CreateByPictureCrosswordActivity.this);
                inputColors.setInputType(InputType.TYPE_CLASS_NUMBER);
                LinearLayout dataView = new LinearLayout(CreateByPictureCrosswordActivity.this);
                dataView.setOrientation(LinearLayout.HORIZONTAL);
                dataView.addView(inputWidth);
                dataView.addView(multiplySign);
                dataView.addView(inputHeight);
                dataView.addView(colorText);
                dataView.addView(inputColors);
                final AlertDialog dialog = new AlertDialog.Builder(CreateByPictureCrosswordActivity.this)
                        .setTitle("Make new crossword")
                        .setMessage("Type in width, height and number of colors to be used:")
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
                        String widthString = inputWidth.getText().toString();
                        String heightString = inputHeight.getText().toString();
                        String colorsString = inputColors.getText().toString();
                        if (widthString.length() == 0 || widthString.length() > 3 ||
                                heightString.length() == 0 || heightString.length() > 3 ||
                                colorsString.length() == 0 || colorsString.length() > 2) {
                            return;
                        }
                        int width = Integer.parseInt(widthString);
                        int height = Integer.parseInt(heightString);
                        int colors = Integer.parseInt(colorsString);
                        if (width < 5 || width > 80 || height < 5 || height > 80 ||
                                colors < 2 || colors > 8) {
                            return;
                        }
                        try {
                            Log.d("CreateByPicture: ", width + " " + height + " " + (colors - 1));
                            Bitmap result = Controller.makeCrosswordViewFromImage(givenImage, width, height, colors - 1);
                            if (Controller.getMadeCrosswordFromLastImage() == null) {
                                Toast nonSuccess = Toast.makeText(CreateByPictureCrosswordActivity.this,
                                        "We don't guarantee the puzzle can be solved", Toast.LENGTH_LONG);
                                nonSuccess.show();
                            }
                            resultImage.setImageBitmap(result);
                            dialog.dismiss();
                        } catch (OutOfMemoryError e) {
                            Toast nonSuccess = Toast.makeText(CreateByPictureCrosswordActivity.this,
                                    "Sorry, the picture was too big.", Toast.LENGTH_LONG);
                            nonSuccess.show();
                            System.gc();
                        }
                    }
                });
            }
        });

        Button saveCrosswordButton = (Button) findViewById(R.id.saveCrosswordButton);
        saveCrosswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText inputName = new EditText(CreateByPictureCrosswordActivity.this);
                final TextView typeInText = new TextView(CreateByPictureCrosswordActivity.this);
                typeInText.setText(R.string.type_in_name);
                final CheckBox saveOnServer = new CheckBox(CreateByPictureCrosswordActivity.this);
                saveOnServer.setText(R.string.save_on_server);
                LinearLayout dataView = new LinearLayout(CreateByPictureCrosswordActivity.this);
                dataView.setOrientation(LinearLayout.VERTICAL);
                dataView.addView(typeInText);
                dataView.addView(inputName);
                dataView.addView(saveOnServer);
                final AlertDialog dialog = new AlertDialog.Builder(CreateByPictureCrosswordActivity.this)
                        .setTitle("Save your crossword")
                        .setView(dataView)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    CurrentCrosswordState state = Controller.getMadeCrosswordFromLastImage();
                                    Log.d("Colors: ", Arrays.toString(state.getColors()));
                                    Controller.addCrosswordLocallyByParametres(state, inputName.getText().toString());
                                    if (saveOnServer.isChecked()) {
                                        Controller.addCrosswordOnServerByParametres(state, inputName.getText().toString());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            givenImage.setImageURI(imageUri);
        }
    }
}

package ru.spbau.nonograms.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.spbau.nonograms.R;

public class PlayMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_menu);


        Button blackAndWhiteButton = (Button) findViewById(R.id.BlackWhiteButton);
        blackAndWhiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayMenuActivity.this, CrosswordListActivity.class);
                intent.putExtra("colored", false);
                startActivity(intent);
            }
        });

        Button coloredButton = (Button) findViewById(R.id.ColorButton);
        coloredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayMenuActivity.this, CrosswordListActivity.class);
                intent.putExtra("colored", true);
                startActivity(intent);
            }
        });

        Button createCrosswordButton = (Button) findViewById(R.id.createCrosswordButton);
        createCrosswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayMenuActivity.this, CreateCrosswordActivity.class);
                startActivity(intent);
            }
        });
    }
}

package ru.spbau.nonograms.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Controller.initDatabase(getApplicationContext());

        Button playMenuButton = (Button) findViewById(R.id.PlayMenuButton);
        playMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playMenu = new Intent(MainActivity.this, PlayMenuActivity.class);
                startActivity(playMenu);
            }
        });
    }
}

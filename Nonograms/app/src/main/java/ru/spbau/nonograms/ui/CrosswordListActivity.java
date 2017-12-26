package ru.spbau.nonograms.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CrosswordInfo;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;

public class CrosswordListActivity extends AppCompatActivity {

    ArrayList<CrosswordInfo> crosswordList;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword_list);

        boolean colored = getIntent().getBooleanExtra("colored", false);
        try {
            crosswordList = Controller.getLocalCrosswords();
        } catch (Exception e) {
            Toast nonSuccess = Toast.makeText(CrosswordListActivity.this,
                    "Couldn't load database", Toast.LENGTH_LONG);
            nonSuccess.show();
            Log.e("Database error", e.getMessage());
        }
        list = new ArrayList<>();
        for (int i = 0; i < crosswordList.size(); i++) {
            if (crosswordList.get(i).getNumberOfColors() == 1 && !colored) {
                list.add(crosswordList.get(i).getFilename());
            }
            if (crosswordList.get(i).getNumberOfColors() > 1 && colored) {
                list.add(crosswordList.get(i).getFilename());
            }
            Log.d("Colors: ", "" + crosswordList.get(i).getNumberOfColors());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, list);
        ListView lv = (ListView) findViewById(R.id.List);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent crossword = new Intent(CrosswordListActivity.this, CrosswordActivity.class);
                crossword.putExtra("Data", list.get(i));
                startActivity(crossword);
            }
        });

    }
}

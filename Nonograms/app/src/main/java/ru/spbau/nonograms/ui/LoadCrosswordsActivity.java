package ru.spbau.nonograms.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.nonograms.R;
import ru.spbau.nonograms.controller.Controller;
import ru.spbau.nonograms.local_database.CrosswordInfo;
import ru.spbau.nonograms.model.NonogramPreview;

public class LoadCrosswordsActivity extends AppCompatActivity {

    NonogramPreview[] crosswordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_crosswords);

        try {
            crosswordList = Controller.getOnServerCrosswords();
        } catch (Exception e) {
            Toast nonSuccess = Toast.makeText(LoadCrosswordsActivity.this,
                    "Couldn't load database", Toast.LENGTH_LONG);
            nonSuccess.show();
            Log.e("Database error", e.getMessage());
        }
        NonogramPreviewArrayAdapter adapter = new NonogramPreviewArrayAdapter(LoadCrosswordsActivity.this, crosswordList);

        ListView lv = (ListView) findViewById(R.id.ListToLoad);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int id, long l) {
                final AlertDialog dialog = new AlertDialog.Builder(LoadCrosswordsActivity.this)
                        .setTitle("Save your crossword")
                        .setMessage("Would you like to save this crossword?")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    Controller.addCrosswordLocallyByPreviewOnServer(crosswordList[id]);
                                } catch (IOException e) {
                                    Log.e("Load crossword:", "couldn't save crossword locally or get it " + e.getMessage());
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

    }

    private class NonogramPreviewArrayAdapter extends ArrayAdapter<NonogramPreview> {
        private final Context context;
        private final NonogramPreview[] values;

        public NonogramPreviewArrayAdapter(Context context, NonogramPreview[] values) {
            super(context, R.layout.nonogram_preview_layout, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.nonogram_preview_layout, parent, false);
            TextView crosswordName = (TextView) rowView.findViewById(R.id.crosswordNameValue);
            TextView authorName = (TextView) rowView.findViewById(R.id.authorNameValue);
            TextView size = (TextView) rowView.findViewById(R.id.sizeValue);
            crosswordName.setText(values[position].name);
            authorName.setText(values[position].author);
            size.setText(values[position].width + "x" + values[position].height);
            return rowView;
        }
    }

}

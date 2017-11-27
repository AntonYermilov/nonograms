package ru.spbau.nonograms.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CrosswordDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CrosswordList";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CROSSWORD_NAME = "crossword_name";

    private Context context;

    public CrosswordDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                 COLUMN_CROSSWORD_NAME + " TEXT )");
        for (int i = 0; i < StandartCrosswords.toAdd.length; i++) {
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_CROSSWORD_NAME, "standart");
                long rowId = db.insert(DATABASE_NAME, null, values);
                try (ObjectOutputStream outputStream =
                             new ObjectOutputStream(context.openFileOutput("standart" + rowId, Context.MODE_PRIVATE))) {
                    outputStream.writeObject(StandartCrosswords.toAdd[i]);
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e("caught an error: ", e.getMessage());
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public void updateByFilename(String filename, CurrentCrosswordState crosswordState) throws IOException {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE))) {
            outputStream.writeObject(crosswordState);
            outputStream.close();
        }
    }

    public void addCrossword(CurrentCrosswordState crosswordState,
                             String name) throws IOException {
        try (SQLiteDatabase database = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CROSSWORD_NAME, name);
            long rowId = database.insert(DATABASE_NAME, null, values);
            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(context.openFileOutput(name + rowId, Context.MODE_PRIVATE))) {
                outputStream.writeObject(crosswordState);
                outputStream.close();
            }
            Log.d("Database", "inserted row number " + rowId);
        }
    }

    public CurrentCrosswordState getCrosswordByFilename(String filename) throws IOException, ClassNotFoundException {
        CurrentCrosswordState result = null;
        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery("SELECT * FROM " + DATABASE_NAME, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_CROSSWORD_NAME));
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    if (filename.equals(name + id)) {
                        result = getCurrentCrosswordStateByCursor(cursor);
                        break;
                    }
                } while (cursor.moveToNext());
            }
        }
        return result;
    }

    public ArrayList<CrosswordInfo> getAllCrosswords() {
        ArrayList<CrosswordInfo> result = new ArrayList<>();
        try (SQLiteDatabase database = this.getReadableDatabase();
             Cursor cursor = database.rawQuery("SELECT * FROM " + DATABASE_NAME, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_CROSSWORD_NAME));
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    CrosswordInfo info = new CrosswordInfo(name + id);
                    result.add(info);
                } while (cursor.moveToNext());
            }
        }
        return result;
    }

    private CurrentCrosswordState getCurrentCrosswordStateByCursor(Cursor cursor)
            throws IOException, ClassNotFoundException {
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_CROSSWORD_NAME));
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        CurrentCrosswordState crossword = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(name + id))) {
            crossword = (CurrentCrosswordState) inputStream.readObject();
        }
        return crossword;
    }
}

package ru.spbau.nonograms.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import ru.spbau.nonograms.local_database.CrosswordDBHelper;
import ru.spbau.nonograms.local_database.CrosswordInfo;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.logic.NonogramImage;
import ru.spbau.nonograms.logic.NonogramLogic;

public class Controller {

    private static CrosswordDBHelper database;

    public static boolean checkCorrectness() {
        return false;
    }

    //pair -- result + crossword
    //receives an image, user posted
    public static Bitmap makeCrosswordViewFromImage(ImageView givenImage) {
        return NonogramLogic.createNonogram(getBitmap(givenImage), 70, 70, 5);
    }

    public static NonogramImage getMadeCrosswordFromLastImage() {
        return NonogramLogic.getLastNonogram();
    }

    public static boolean checkCorrectness(CurrentCrosswordState current) {
        for (int i = 0; i < current.getWidth(); i++) {
            for (int j = 0; j < current.getHeight(); j++) {
                if (current.getField(i, j).getValue() != CurrentCrosswordState.FILLED_CELL) {
                    current.setField(i, j, new CurrentCrosswordState.ColoredValue(
                            current.getField(i, j).getValue(), Color.WHITE));
                }
            }
        }
        return NonogramLogic.checkNonogram(current);
    }

    public static void initDatabase(Context context) {
        database = new CrosswordDBHelper(context);
    }

    public static ArrayList<CrosswordInfo> getLocalCrosswords() throws IOException, ClassNotFoundException {
        return database.getAllCrosswords();
    }

    public static CurrentCrosswordState getLocalCrosswordByFilename(String filename) throws IOException, ClassNotFoundException {
        return database.getCrosswordByFilename(filename);
    }

    public static void updateLocalyByFilename(String filename, CurrentCrosswordState state) throws IOException {
        database.updateByFilename(filename, state);
    }

    private static Bitmap getBitmap(ImageView givenImage) {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) givenImage.getDrawable());
        Bitmap bitmap;
        if (bitmapDrawable == null) {
            givenImage.buildDrawingCache();
            bitmap = givenImage.getDrawingCache();
            givenImage.buildDrawingCache(false);
        } else {
            bitmap = bitmapDrawable.getBitmap();
        }
        return bitmap;
    }

}

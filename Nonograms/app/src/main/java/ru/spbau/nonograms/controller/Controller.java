package ru.spbau.nonograms.controller;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.logic.NonogramImage;
import ru.spbau.nonograms.logic.NonogramLogic;

public class Controller {

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
        ArrayList<NonogramImage.Segment>[] rows = makeSegmentArrayList(current.getRows());
        ArrayList<NonogramImage.Segment>[] columns = makeSegmentArrayList(current.getColumns());
        NonogramImage toCheck = new NonogramImage(current.getHeight(), current.getWidth(),
                Color.WHITE, rows, columns);
        int[][] field = new int[current.getHeight()][current.getWidth()];
        for (int i = 0; i < current.getHeight(); i++) {
            for (int j = 0; j < current.getWidth(); j++) {
                field[i][j] = current.getField(j, i) == CurrentCrosswordState.FILLED_CELL ?
                        Color.BLACK : Color.WHITE;
            }
        }
        return NonogramLogic.checkNonogram(field, toCheck);
    }

    private static ArrayList<NonogramImage.Segment>[] makeSegmentArrayList(int[][] old) {
        ArrayList<NonogramImage.Segment>[] result = new ArrayList[old.length];
        for (int i = 0; i < old.length; i++) {
            result[i] = new ArrayList<>();
            for (int j = 0; j < old[i].length; j++) {
                NonogramImage.Segment block = new NonogramImage.Segment(old[i][j], 1, Color.BLACK);
                result[i].add(block);
            }
        }
        return result;
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

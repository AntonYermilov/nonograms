package ru.spbau.nonograms.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import ru.spbau.nonograms.logic.NonogramLogic;

public class Controller {

    public static boolean checkCorrectness() {
        return false;
    }

    //pair -- result + crossword
    //receives an image, user posted
    public static Bitmap makeCrosswordViewFromImage(ImageView givenImage) {
        NonogramLogic result = new NonogramLogic(getBitmap(givenImage));
        return result.showNonogram(20, 20, 5);
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

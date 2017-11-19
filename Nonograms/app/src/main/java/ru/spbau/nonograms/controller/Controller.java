package ru.spbau.nonograms.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

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

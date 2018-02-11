package ru.spbau.nonograms.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.spbau.nonograms.client.ClientManager;
import ru.spbau.nonograms.local_database.CrosswordDBHelper;
import ru.spbau.nonograms.local_database.CrosswordInfo;
import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.logic.NonogramImage;
import ru.spbau.nonograms.logic.NonogramLogic;
import ru.spbau.nonograms.model.Image;
import ru.spbau.nonograms.model.NonogramPreview;

/**
 * Controller connects all parts of the program.
 * Sometimes it does some "translation" between different parts.
 */
public class Controller {

    private static CrosswordDBHelper database;

    //pair -- result + crossword
    //receives an image, user posted
    public static Bitmap makeCrosswordViewFromImage(ImageView givenImage, int width, int height, int colors) {
        return NonogramLogic.createNonogram(getBitmap(givenImage), width, height, colors);
    }

    public static CurrentCrosswordState getMadeCrosswordFromLastImage() {
        return NonogramLogic.getLastNonogram();
    }

    boolean canBeSolvedOnServer(Image image) {
        return ClientManager.solveNonogram(image);
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

    public static NonogramPreview[] getOnServerCrosswords() {
        return ClientManager.getNonogramPreviewInfo();
    }

    public static CurrentCrosswordState getLocalCrosswordByFilename(String filename)
            throws IOException, ClassNotFoundException {
        return database.getCrosswordByFilename(filename);
    }

    public static void updateLocalyByFilename(String filename, CurrentCrosswordState state) throws IOException {
        database.updateByFilename(filename, state);
    }

    public static void addCrosswordLocallyByField(CurrentCrosswordState crossword, String name) throws IOException {
        NonogramImage ni = new NonogramImage(getClearFieldFromCrosswordState(crossword), Color.WHITE);
        database.addCrossword(transferNonogramImageToCrosswordState(ni), name, "anonymous");
    }

    public static void addCrosswordLocallyByParametres(CurrentCrosswordState crossword, String name) throws IOException {
        database.addCrossword(crossword, name, "anonymous");
    }

    public static void addCrosswordLocallyByPreviewOnServer(NonogramPreview preview) throws IOException {
        CurrentCrosswordState crossword = ClientManager.getNonogramById(preview.id);
        database.addCrossword(crossword, preview.name, preview.author);
    }

    public static Image createImageOnServer(Image image) {
        return ClientManager.createNonogram(image);
    }

    public static void addCrosswordOnServerByField(CurrentCrosswordState current, String crosswordName) {
        NonogramImage ni = new NonogramImage(getClearFieldFromCrosswordState(current), Color.WHITE);
        ClientManager.saveNonogram(transferNonogramImageToCrosswordState(ni), crosswordName, "anonymous");
    }

    public static void addCrosswordOnServerByParametres(CurrentCrosswordState state, String crosswordName) {
        ClientManager.saveNonogram(state, crosswordName, "anonymous");
    }

    private static CurrentCrosswordState transferNonogramImageToCrosswordState(NonogramImage ni) {
        int[] usedColors = Arrays.copyOfRange(ni.getUsedColors(), 1, ni.getUsedColors().length);
        int width = ni.getWidth();
        int height = ni.getHeight();
        CurrentCrosswordState.ColoredValue[][] columns = new CurrentCrosswordState.ColoredValue[width][];
        for (int i = 0; i < width; i++) {
            List<NonogramImage.Segment> col = ni.getColumn(i);
            columns[i] = new CurrentCrosswordState.ColoredValue[col.size()];
            for (int j = 0; j < columns[i].length; j++) {
                columns[i][j] = new CurrentCrosswordState.ColoredValue(col.get(j).getSize(),
                        ni.getRGBColor(col.get(j).getColorType()));
            }

        }
        CurrentCrosswordState.ColoredValue[][] rows = new CurrentCrosswordState.ColoredValue[height][];
        for (int i = 0; i < height; i++) {
            List<NonogramImage.Segment> row = ni.getRow(i);
            rows[i] = new CurrentCrosswordState.ColoredValue[row.size()];
            for (int j = 0; j < rows[i].length; j++) {
                rows[i][j] = new CurrentCrosswordState.ColoredValue(row.get(j).getSize(),
                        ni.getRGBColor(row.get(j).getColorType()));
            }

        }
        return new CurrentCrosswordState(rows, columns, usedColors, Color.WHITE, null);
    }

    private static int[][] getClearFieldFromCrosswordState(CurrentCrosswordState state) {
        int[][] result = new int[state.getHeight()][state.getWidth()];
        for (int i = 0; i < state.getHeight(); i++) {
            for (int j = 0; j < state.getWidth(); j++) {
                CurrentCrosswordState.ColoredValue val = state.getField(j, i);
                if (val.getValue() == CurrentCrosswordState.FILLED_CELL) {
                    result[i][j] = val.getColor();
                } else {
                    result[i][j] = Color.WHITE;
                }
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

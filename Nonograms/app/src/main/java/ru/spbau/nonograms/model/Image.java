package ru.spbau.nonograms.model;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

import ru.spbau.nonograms.local_database.CurrentCrosswordState;

import static ru.spbau.nonograms.local_database.CurrentCrosswordState.ColoredValue;

/**
 * Stores image as array of pixels.
 * Supports creating from Bitmap and CurrentCrosswordState.
 * Is used for interaction with server.
 */
public class Image {

    private int height;
    private int width;
    private int colors;
    private int backgroundColor;
    private int[] pixels;

    /**
     * Transforms bitmap to image. Also receives number of colors that we want to have.
     * It's important to use bitmap with ARGB_8888 configuration type.
     * @param bitmap image stored in bitmap
     * @param colors expected number of colors (excluding background color)
     */
    public Image(Bitmap bitmap, int colors) {
        this.height = bitmap.getHeight();
        this.width = bitmap.getWidth();
        this.colors = colors;
        this.backgroundColor = -1;
        this.pixels = new int[this.height * this.width];
        bitmap.getPixels(this.pixels, 0, this.width, 0, 0, this.width, this.height);
    }

    /**
     * Creates image from specified array of colors of cells. Also receives number
     * of colors in image and color value of its background.
     * @param field specified array of colors
     * @param colors number of different colors in array (excluding background color)
     * @param backgroundColor image background color
     */
    public Image(int[][] field, int colors, int backgroundColor) {
        this.height = field.length;
        this.width = field[0].length;
        this.colors = colors;
        this.backgroundColor = backgroundColor;
        this.pixels = new int[this.height * this.width];
        for (int i = 0; i < this.height; i++) {
            System.arraycopy(field[i], 0, this.pixels, i * this.width, this.width);
        }
    }

    /**
     * Transforms nonogram field to image.
     * @param state current nonogram state
     */
    public Image(CurrentCrosswordState state) {
        this.height = state.getHeight();
        this.width = state.getWidth();
        this.colors = state.getColors().length;

        //TODO backgroundColor
        //this.backgroundColor = state.getBackgroundColor();

        this.pixels = new int[this.height * this.width];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                this.pixels[y * this.width + x] = state.getField(x, y).getColor();
            }
        }
    }

    /**
     * Creates image from one that is stored in json object.
     * @param jsonObject specified json object that stores image
     */
    public Image(JsonObject jsonObject) {
        height = jsonObject.get("height").getAsInt();
        width = jsonObject.get("width").getAsInt();
        colors = jsonObject.get("colors").getAsInt();
        backgroundColor = jsonObject.get("backgroundColor").getAsInt();

        String buffer = jsonObject.get("pixels").getAsString();
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer.getBytes(StandardCharsets.UTF_8))
                .order(ByteOrder.LITTLE_ENDIAN);
        pixels = byteBuffer.asIntBuffer().array();
    }

    /**
     * Transforms image to bitmap with ARGB_8888 configuration type.
     * @return bitmap created using the stored array of pixels
     */
    public Bitmap toBitmap() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public CurrentCrosswordState toCurrentCrosswordState() {
        int[] imageColors = new int[colors];
        HashSet<Integer> used = new HashSet<>();
        for (int color : pixels) {
            if (color != backgroundColor && !used.contains(color)) {
                imageColors[used.size()] = color;
                used.add(color);
            }
        }

        ColoredValue[][] rows = new ColoredValue[height][];
        for (int i = 0; i < height; i++) {
            ArrayList<ColoredValue> blocks = new ArrayList<>();
            for (int l = 0, r = 0; r < width; l = r) {
                while (r < width && pixels[i * width + l] == pixels[i * width + r]) {
                    r++;
                }
                if (pixels[i * width + l] != backgroundColor) {
                    blocks.add(new ColoredValue(r - l, pixels[i * width + l]));
                }
            }
            rows[i] = (ColoredValue[]) blocks.toArray();
        }

        ColoredValue[][] columns = new ColoredValue[width][];
        for (int i = 0; i < width; i++) {
            ArrayList<ColoredValue> blocks = new ArrayList<>();
            for (int l = 0, r = 0; r < height; l = r) {
                while (r < height && pixels[l * width + i] == pixels[r * width + i]) {
                    r++;
                }
                if (pixels[l * width + i] != backgroundColor) {
                    blocks.add(new ColoredValue(r - l, pixels[l * width + i]));
                }
            }
            columns[i] = (ColoredValue[]) blocks.toArray();
        }

        //TODO add backgroundColor
        //return new CurrentCrosswordState(rows, columns, imageColors, backgroundColor, null);
        return new CurrentCrosswordState(rows, columns, imageColors, null);
    }

    /**
     * Transforms image to json with the same fields.
     * @return json object that represents this image
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("height", height);
        jsonObject.addProperty("width", height);
        jsonObject.addProperty("colors", colors);
        jsonObject.addProperty("backgroundColor", backgroundColor);

        ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4)
                .order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.asIntBuffer().put(pixels);
        jsonObject.addProperty("pixels", new String(byteBuffer.array(), StandardCharsets.UTF_8));

        return jsonObject;
    }

}

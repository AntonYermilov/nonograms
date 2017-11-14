package ru.spbau.nonograms.logic;

import java.util.ArrayList;
import java.util.HashMap;

public class NonogramImage {
    public static int MAX_COLORS = 7;

    private int height;
    private int width;
    private int colors;
    private ArrayList<Segment> rows[];
    private ArrayList<Segment> columns[];

    private int backgroundColor;
    private Color usedColors[];

    NonogramImage(int[][] field, int backgroundColor) throws ColorOutOfRangeException {
        height = field.length;
        width = field[0].length;
        colors = 0;

        this.backgroundColor = backgroundColor;
        System.err.println(backgroundColor);

        HashMap<Integer, Integer> colorId = new HashMap<>();
        colorId.put(backgroundColor, 0);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!colorId.containsKey(field[i][j])) {
                    colorId.put(field[i][j], ++colors);
                }
            }
        }

        if (colors > MAX_COLORS) {
            throw new ColorOutOfRangeException();
        }

        usedColors = new Color[colorId.size()];
        int nextColor = 0;
        for (int rgbColor : colorId.keySet()) {
            System.err.print(rgbColor + " ");
            usedColors[nextColor++] = new Color(colorId.get(rgbColor), rgbColor);
        }
        System.err.println();

        rows = new ArrayList[height];
        for (int i = 0; i < height; i++) {
            rows[i] = new ArrayList<>();
            for (int l = 0, r = 0; r < width; l = r) {
                while (r < width && field[i][l] == field[i][r]) {
                    r++;
                }
                if (field[i][l] != backgroundColor) {
                    rows[i].add(new Segment(r - l, colorId.get(field[i][l]), field[i][l]));
                }
            }
        }

        columns = new ArrayList[width];
        for (int i = 0; i < width; i++) {
            columns[i] = new ArrayList<>();
            for (int l = 0, r = 0; r < height; l = r) {
                while (r < height && field[l][i] == field[r][i]) {
                    r++;
                }
                if (field[l][i] != backgroundColor) {
                    columns[i].add(new Segment(r - l, colorId.get(field[l][i]), field[l][i]));
                }
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getColors() {
        return colors;
    }

    public ArrayList<Segment> getRow(int row) {
        return new ArrayList<>(rows[row]);
    }

    public ArrayList<Segment> getColumn(int column) {
        return new ArrayList<>(columns[column]);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public Color[] getUsedColors() {
        return usedColors;
    }

    public static class Segment {
        private int size;
        private Color color;

        Segment(int size, int colorType, int rgbColor) {
            this.size = size;
            this.color = new Color(colorType, rgbColor);
        }

        public int getSize() {
            return size;
        }

        public int getColorType() {
            return color.colorType;
        }

        public int getRGBColor() {
            return color.rgbColor;
        }
    }

    public static class Color {
        private int colorType;
        private int rgbColor;

        Color(int colorType, int rgbColor) {
            this.colorType = colorType;
            this.rgbColor = rgbColor;
        }

        public int getColorType() {
            return colorType;
        }

        public int getRGBColor() {
            return rgbColor;
        }
    }

    public static class ColorOutOfRangeException extends Exception {
        ColorOutOfRangeException() {
            super("Incorrect field. Not more than " + MAX_COLORS + "colors, not counting" +
                    "the background color, can be used in nonogram.");
        }
    }
}

package ru.spbau.nonograms.local_database;

public class CrosswordInfo {

    private String filename;
    private int numberOfColors;

    public CrosswordInfo(String filename, int numberOfColors) {
        this.filename = filename;
        this.numberOfColors = numberOfColors;
    }

    public String getFilename() {
        return filename;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }
}

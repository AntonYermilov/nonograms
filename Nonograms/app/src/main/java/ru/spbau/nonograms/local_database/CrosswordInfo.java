package ru.spbau.nonograms.local_database;

public class CrosswordInfo {

    private String crosswordName;
    private String authorName;
    private int numberOfColors;
    private int id;

    public CrosswordInfo(String crosswordName, String authorName, int numberOfColors, int id) {
        this.crosswordName = crosswordName;
        this.authorName = authorName;
        this.numberOfColors = numberOfColors;
        this.id = id;
    }

    public String getCrosswordName() {
        return crosswordName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getFilename() {
        return crosswordName + id;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }
}

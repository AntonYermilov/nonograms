package ru.spbau.nonograms.model;

/**
 * Describes some base information about nonogram, such as width,
 * height, name and author.
 */
public class NonogramPreview {
    public final int id;
    public final String name;
    public final String author;
    public final int height;
    public final int width;

    public NonogramPreview(int id, String name, String author, int height, int width) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.height = height;
        this.width = width;
    }
}

package ru.spbau.nonograms.local_database;

/** A class, storing information about some crossword. */
public class CrosswordInfo {

    private String crosswordName;
    private String authorName;
    private int numberOfColors;
    private int id;

    /**
     * To create an information item.
     * @param crosswordName a name of the crossword
     * @param authorName an author of the crossword
     * @param numberOfColors number of colors in the crossword
     * @param id a position in the database
     */
    public CrosswordInfo(String crosswordName, String authorName, int numberOfColors, int id) {
        this.crosswordName = crosswordName;
        this.authorName = authorName;
        this.numberOfColors = numberOfColors;
        this.id = id;
    }

    /**
     * Returns name of the crossword
     * @return name of the crossword
     */
    public String getCrosswordName() {
        return crosswordName;
    }

    /**
     * Returns name of the author
     * @return name of the author
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Returns name of a file, where crossword stored.
     * @return name of a file, where crossword stored.
     */
    public String getFilename() {
        return crosswordName + id;
    }

    /**
     * Returns number of colors in the crosswords.
     * @return number of colors in the crosswords.
     */
    public int getNumberOfColors() {
        return numberOfColors;
    }
}

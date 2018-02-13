package ru.spbau.nonograms.local_database;

import android.graphics.Color;
import static ru.spbau.nonograms.local_database.CurrentCrosswordState.ColoredValue;

/**
 * A class with standart crosswords stored inside.
 * Includes useful definitions, like arrays of ColoredValue with the sam color,
 * but different numbers.
 */
public class StandartCrosswords {

    private final static ColoredValue[] black =
            new ColoredValue[] {
                    new ColoredValue(1, Color.BLACK),
                    new ColoredValue(2, Color.BLACK),
                    new ColoredValue(3, Color.BLACK),
            };

    private final static ColoredValue[] green =
            new ColoredValue[] {
                    new ColoredValue(1, Color.GREEN),
                    new ColoredValue(2, Color.GREEN),
                    new ColoredValue(3, Color.GREEN),
            };

    private final static ColoredValue[] blue =
            new ColoredValue[] {
                    new ColoredValue(1, Color.BLUE),
                    new ColoredValue(2, Color.BLUE),
                    new ColoredValue(3, Color.BLUE),
            };

    private final static ColoredValue[] yellow = new ColoredValue[] {
                    new ColoredValue(1, Color.YELLOW),
                    new ColoredValue(2, Color.YELLOW),
                    new ColoredValue(3, Color.YELLOW),
            };
    /** Array of standart crosswords. */
    public static final CurrentCrosswordState toAdd[] = new CurrentCrosswordState[] {
            new CurrentCrosswordState(new ColoredValue[][] {
                    {black[0], black[0]},
                    {black[0], black[0], black[0]},
                    {black[0], black[0]},
                    {black[0], black[0]},
                    {black[0]}
            }, new ColoredValue[][] {
                    {black[1]},
                    {black[0], black[0]},
                    {black[0], black[0]},
                    {black[0], black[0]},
                    {black[1]}
            }, new int[] {Color.BLACK}, Color.WHITE, null),
            new CurrentCrosswordState(new ColoredValue[][] {
                    {black[0], black[0]},
                    {black[0], black[0], black[0], black[0]},
                    {},
                    {black[0], black[0]},
                    {black[2]}
            }, new ColoredValue[][] {
                    {black[0]},
                    {black[0]},
                    {black[0], black[1]},
                    {black[0]},
                    {black[0], black[1]},
                    {black[0]},
                    {black[0]}
            }, new int[] {Color.BLACK}, Color.WHITE, null),
            new CurrentCrosswordState(new ColoredValue[][] {
                    {black[0], blue[2], black[0]},
                    {green[0], black[0], blue[0], black[0], green[0]},
                    {green[1], black[0], green[1]},
                    {green[0], black[0], yellow[0], black[0], green[0]},
                    {black[0], yellow[2], black[0]}
            }, new ColoredValue[][] {
                    {black[0], green[2] , black[0]},
                    {blue[0], black[0], green[0], black[0], yellow[0]},
                    {blue[1], black[0], yellow[1]},
                    {blue[0], black[0], green[0], black[0], yellow[0]},
                    {black[0], green[2], black[0]}
            }, new int[] {Color.BLACK, Color.GREEN, Color.BLUE, Color.YELLOW}, Color.WHITE, null)
    };
}

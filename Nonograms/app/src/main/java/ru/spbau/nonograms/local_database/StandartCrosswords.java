package ru.spbau.nonograms.local_database;

import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ru.spbau.nonograms.logic.NonogramImage;

public class StandartCrosswords {

    private final static CurrentCrosswordState.ColoredValue[] black =
            new CurrentCrosswordState.ColoredValue[] {
                    new CurrentCrosswordState.ColoredValue(1, Color.BLACK),
                    new CurrentCrosswordState.ColoredValue(2, Color.BLACK),
                    new CurrentCrosswordState.ColoredValue(3, Color.BLACK),
            };

    private final static CurrentCrosswordState.ColoredValue[] green =
            new CurrentCrosswordState.ColoredValue[] {
                    new CurrentCrosswordState.ColoredValue(1, Color.GREEN),
                    new CurrentCrosswordState.ColoredValue(2, Color.GREEN),
                    new CurrentCrosswordState.ColoredValue(3, Color.GREEN),
            };

    private final static CurrentCrosswordState.ColoredValue[] blue =
            new CurrentCrosswordState.ColoredValue[] {
                    new CurrentCrosswordState.ColoredValue(1, Color.BLUE),
                    new CurrentCrosswordState.ColoredValue(2, Color.BLUE),
                    new CurrentCrosswordState.ColoredValue(3, Color.BLUE),
            };

    private final static CurrentCrosswordState.ColoredValue[] yellow =
            new CurrentCrosswordState.ColoredValue[] {
                    new CurrentCrosswordState.ColoredValue(1, Color.YELLOW),
                    new CurrentCrosswordState.ColoredValue(2, Color.YELLOW),
                    new CurrentCrosswordState.ColoredValue(3, Color.YELLOW),
            };
            
    public static final CurrentCrosswordState toAdd[] = new CurrentCrosswordState[] {
            new CurrentCrosswordState(new CurrentCrosswordState.ColoredValue[][] {
                    {black[0], black[0]},
                    {black[0], black[0], black[0]},
                    {black[0], black[0]},
                    {black[0], black[0]},
                    {black[0]}
            }, new CurrentCrosswordState.ColoredValue[][] {
                    {black[1]},
                    {black[0], black[0]},
                    {black[0], black[0]},
                    {black[0], black[0]},
                    {black[1]}
            }, new int[] {Color.BLACK}, Color.WHITE, null),
            new CurrentCrosswordState(new CurrentCrosswordState.ColoredValue[][] {
                    {black[0], black[0]},
                    {black[0], black[0], black[0], black[0]},
                    {},
                    {black[0], black[0]},
                    {black[2]}
            }, new CurrentCrosswordState.ColoredValue[][] {
                    {black[0]},
                    {black[0]},
                    {black[0], black[1]},
                    {black[0]},
                    {black[0], black[1]},
                    {black[0]},
                    {black[0]}
            }, new int[] {Color.BLACK}, Color.WHITE, null),
            new CurrentCrosswordState(new CurrentCrosswordState.ColoredValue[][] {
                    {black[0], blue[2], black[0]},
                    {green[0], black[0], blue[0], black[0], green[0]},
                    {green[1], black[0], green[1]},
                    {green[0], black[0], yellow[0], black[0], green[0]},
                    {black[0], yellow[2], black[0]}
            }, new CurrentCrosswordState.ColoredValue[][] {
                    {black[0], green[2] , black[0]},
                    {blue[0], black[0], green[0], black[0], yellow[0]},
                    {blue[1], black[0], yellow[1]},
                    {blue[0], black[0], green[0], black[0], yellow[0]},
                    {black[0], green[2], black[0]}
            }, new int[] {Color.BLACK, Color.GREEN, Color.BLUE, Color.YELLOW}, Color.WHITE, null)
    };
}

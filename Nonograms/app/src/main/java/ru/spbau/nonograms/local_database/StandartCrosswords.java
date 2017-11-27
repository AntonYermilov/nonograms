package ru.spbau.nonograms.local_database;

import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StandartCrosswords {

    private static CurrentCrosswordState.ColoredValue blackOne =
            new CurrentCrosswordState.ColoredValue(1, Color.BLACK);
    private static CurrentCrosswordState.ColoredValue blackTwo =
            new CurrentCrosswordState.ColoredValue(2, Color.BLACK);
    private static CurrentCrosswordState.ColoredValue blackThree =
            new CurrentCrosswordState.ColoredValue(3, Color.BLACK);

    public static CurrentCrosswordState toAdd[] = new CurrentCrosswordState[] {
            new CurrentCrosswordState(new CurrentCrosswordState.ColoredValue[][] {
                    {blackOne, blackOne},
                    {blackOne, blackOne, blackOne},
                    {blackOne, blackOne},
                    {blackOne, blackOne},
                    {blackOne}
            }, new CurrentCrosswordState.ColoredValue[][] {
                    {blackTwo},
                    {blackOne, blackOne},
                    {blackOne, blackOne},
                    {blackOne, blackOne},
                    {blackTwo}
            }, null),
            new CurrentCrosswordState(new CurrentCrosswordState.ColoredValue[][] {
                    {blackOne, blackOne},
                    {blackOne, blackOne, blackOne, blackOne},
                    {},
                    {blackOne, blackOne},
                    {blackThree}
            }, new CurrentCrosswordState.ColoredValue[][] {
                    {blackOne},
                    {blackOne},
                    {blackOne, blackTwo},
                    {blackOne},
                    {blackOne, blackTwo},
                    {blackOne},
                    {blackOne}
            }, null)
    };
}

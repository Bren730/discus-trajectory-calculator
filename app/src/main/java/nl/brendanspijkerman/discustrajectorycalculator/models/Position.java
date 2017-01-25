package nl.brendanspijkerman.discustrajectorycalculator.models;

/**
 * Created by Brendan on 25-1-2017.
 */

public class Position {

    public double[] coordinates = new double[3];
    public long timestamp;

    public Position(double[] _coordinates, long _timestamp) {

        if (_coordinates.length == 3) {
            coordinates = _coordinates;
            timestamp = _timestamp;
        } else {

            throw new IllegalArgumentException();

        }


    }

}

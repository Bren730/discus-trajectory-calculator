package nl.brendanspijkerman.discustrajectorycalculator;

/**
 * Created by Brendan on 12-11-2016.
 */

public class Trajectory {

    String model;
    double[] data;
    double xMax;
    double yMax;
    double flightTime;
    Variables variables;

    Trajectory(double[] _data, double _xMax, double _yMax, double _flightTime, Variables _variables, String _model) {

        data = _data;
        xMax = _xMax;
        yMax = _yMax;
        flightTime = _flightTime;
        variables = _variables;
        model = _model;

    }

}

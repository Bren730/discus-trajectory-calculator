package nl.brendanspijkerman.discustrajectorycalculator;

import java.util.ArrayList;

/**
 * Created by Brendan on 12-11-2016.
 */

public class Trajectory {

    String model;
    int[] modelVersion = new int[3];
    ArrayList<Telemetry> data = new ArrayList<Telemetry>();
    double xMax;
    double yMax;
    double finalDistance;
    double flightTime;
    Variables variables;

    Trajectory(ArrayList<Telemetry> _data, double _xMax, double _yMax, double _finalDistance, double _flightTime, Variables _variables, String _model, int[] _modelVersion) {

        data = _data;
        xMax = _xMax;
        yMax = _yMax;
        finalDistance = _finalDistance;
        flightTime = _flightTime;
        variables = _variables;
        model = _model;
        modelVersion = _modelVersion;

    }

}

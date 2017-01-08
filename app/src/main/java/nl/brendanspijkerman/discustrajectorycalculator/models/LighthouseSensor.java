package nl.brendanspijkerman.discustrajectorycalculator.models;

import org.opencv.core.Point3;

/**
 * Created by Brendan on 8-1-2017.
 */

public class LighthouseSensor {

    public int id;
    public int deltaT;
    public Point3 position3D = new Point3();
    public double[] position2D = new double[2];
    public double[] angles = new double[2];
    public boolean sawSweep;

    public LighthouseSensor(int _id, Point3 _position) {

        id = _id;
        position3D = _position;

    }

}

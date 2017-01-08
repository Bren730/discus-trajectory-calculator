package nl.brendanspijkerman.discustrajectorycalculator.models;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;

/**
 * Created by Brendan on 8-1-2017.
 */

public class BaseStation {

    int id;
    int res = 1000;
    double halfRes = (double)res / 2.0;

    public double fov;
    public double fx;
    public double fy;
    public double cx;
    public double cy;

    public byte skip;
    public byte rotor;
    public byte data;

    public Mat cameraMatrix = new Mat(3, 3, CvType.CV_64FC1);
    // Distortioncoefficients are an empty Mat since the signal from the BaseStation can be
    // treated as a perfect camera (no distortion)
    public MatOfDouble distortionCoefficients = new MatOfDouble();

    public BaseStation(double _fov) {

        double fov = _fov;
        double fx = (double)res / (2 * Math.tan(fov / 180.0 * Math.PI / 2.0));
        double fy = fx;
        double cx = (double)res / 2.0;
        double cy = cx;

        cameraMatrix.put(0, 0, fx);
        cameraMatrix.put(0, 2, cx);
        cameraMatrix.put(1, 1, fy);
        cameraMatrix.put(1, 2, cy);
        cameraMatrix.put(2, 2, 1);

    }

}

package nl.brendanspijkerman.discustrajectorycalculator.models;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Brendan on 8-1-2017.
 */

public class DataDiscus {

    String name;
    String id;
    public int sensorCount;
    public double radius;
    public double zOffset;
    BaseStation baseStation;
    public ArrayList<LighthouseSensor> sensors = new ArrayList<>();

    // Header metadata length
    int headerLength = 1;
    // Message length for each sensor. 1 Byte indicating sensor id and 4 bytes of timing data
    int msgLength = 5;

    public MatOfPoint3f objPoints = new MatOfPoint3f();
    public ArrayList<Point3> objPointsList = new ArrayList<>();

    public double[] position = new double[3];
    public double[] rotation = new double[3];

    final double CPU_SPEED = 96.0; // CPU speed in MHz
    final double SWEEP_CYCLE_TIME = 8333; // Sweep cycle time in us
    final double SWEEP_CYCLE_CLOCK_CYCLES = SWEEP_CYCLE_TIME * CPU_SPEED; // Amount of CPU cycles per sweep

    public DataDiscus(int _sensorCount, double _radius, double _zOffset, BaseStation _baseStation) {

        sensorCount = _sensorCount;
        radius = _radius;
        zOffset = _zOffset;
        baseStation = _baseStation;

        objPoints = constructSensorPositions(sensorCount, radius, zOffset);

        for(int i = 0; i < sensorCount; i++) {

            LighthouseSensor lighthouseSensor = new LighthouseSensor(i, objPointsList.get(i));

            sensors.add(lighthouseSensor);

        }

    }

    private MatOfPoint3f constructSensorPositions(int corners, double radius, double zOffset) {

        MatOfPoint3f result = new MatOfPoint3f();
        List<Point3> pointsList = new ArrayList<>();

        double angle = 360.0 / (double)corners;

        for (int i = 0; i < corners; i++) {

            Point3 point = new Point3(Math.cos(Math.toRadians(angle * i)) * radius, Math.sin(Math.toRadians(angle * i)) * radius, zOffset);
            pointsList.add(point);
            objPointsList.add(point);

        }

        result.fromList(pointsList);

        return result;

    }

    public void parseData(int[] data) {

        if (data.length > 0) {

            resetSensors();

            int observedSensorCount = (int)Math.floor( (double)(data.length - headerLength) / (double)msgLength );

            int meta = data[0];

            baseStation.skip = (byte)getBit(meta, 2);
            baseStation.rotor = (byte)getBit(meta, 1);
            baseStation.data = (byte)getBit(meta, 0);

            if (observedSensorCount > 0) {

                ArrayList<Point> observedImgPointsList = new ArrayList<>();
                ArrayList<Point3> observedObjPointsList = new ArrayList<>();

                for (int i = 0; i < observedSensorCount; i++) {

                    int readPos = (i * msgLength) + headerLength;
                    byte sensorId = (byte)data[readPos];

                    LighthouseSensor _sensor = sensors.get(sensorId);

                    _sensor.deltaT = (((data[readPos + 1] & 0xFF) << 24) | ((data[readPos + 2] & 0xFF) << 16) | ((data[readPos + 3] & 0xFF) << 8) | (data[readPos + 4] & 0xFF));

                    _sensor.sawSweep = true;

                    if (baseStation.rotor == 0) {

                        _sensor.angles[0] = getAngle(_sensor.deltaT);
                        _sensor.position2D[0] = getScreenX(_sensor.angles[0]);

                    } else {

                        _sensor.angles[1] = getAngle(_sensor.deltaT);
                        _sensor.position2D[1] = getScreenX(_sensor.angles[1]);

                    }

                    Point point = new Point(_sensor.position2D[0], _sensor.position2D[1]);

                    // Populate the image and object points lists with the sensors that were observed
                    observedImgPointsList.add(point);
                    observedObjPointsList.add(objPointsList.get(sensorId));

                }

                MatOfPoint2f _imgPoints = new MatOfPoint2f();
                MatOfPoint3f _objPoints = new MatOfPoint3f();

                _imgPoints.fromList(observedImgPointsList);
                _objPoints.fromList(observedObjPointsList);

                solvePnP(_objPoints, _imgPoints);

            }

        }

    }

    private double getAngle(int deltaT) {

        double angle = 0;

        double fovLimit = (180 - baseStation.fov) / 2.0;

        angle = ((double)deltaT / SWEEP_CYCLE_CLOCK_CYCLES) * 180;

        if (angle < fovLimit || angle > 180 - fovLimit) {

            angle = 0;
        }

        return angle;
    }

    double getScreenX(double angle) {

        angle = -1 * angle;

        double xPos;
        double max = Math.tan((Math.toRadians(baseStation.fov) / 2.0));
        double perc = Math.tan(angle) / max;
        //println(max, perc, angle);
        xPos = baseStation.halfRes + (perc * baseStation.halfRes);

        return xPos;

    }

    private void resetSensors() {

        for (LighthouseSensor sensor: sensors) {

            sensor.sawSweep = false;

        }

    }

    public MatOfPoint2f constructImgPoints() {

        MatOfPoint2f _imgPoints = new MatOfPoint2f();

        return _imgPoints;

    }

    private void solvePnP(MatOfPoint3f _objPoints, MatOfPoint2f _imgPoints) {

        Mat outputR = new Mat(3, 1, CvType.CV_64FC1);
        Mat outputT = new Mat(3, 1, CvType.CV_64FC1);

        try {

            Calib3d.solvePnP(_objPoints, _imgPoints, baseStation.cameraMatrix, baseStation.distortionCoefficients, outputR, outputT);

            Mat rMat = new Mat();
            Calib3d.Rodrigues(outputR, rMat);

        } catch (Exception e) {

            Log.e("ERROR", e.toString());

        }

    }

    private int getBit(int number, int position) {
        return (number >> position) & 1;
    }

    private boolean getBitAsBoolean(int number, int position) {

        byte val = (byte)getBit(number, position);

        if (val == 0) {
            return false;
        } else {
            return true;
        }

    }

}

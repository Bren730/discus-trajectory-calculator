package nl.brendanspijkerman.discustrajectorycalculator.models;

import android.util.Log;
import android.widget.TextView;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.w3c.dom.Text;

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
    // Start flag length
    int startFlagLength = 2;
    // Message length for each sensor. 1 Byte indicating sensor id and 4 bytes of timing data
    int msgLength = 5;

    public MatOfPoint3f objPoints = new MatOfPoint3f();
    public ArrayList<Point3> objPointsList = new ArrayList<>();

    // 120 measurements per second, for five seconds
    public ArrayList<double[]> position = new ArrayList<>(120 * 5);
    public ArrayList<double[]> rotation = new ArrayList<>(120 * 5);
    public double velocity;

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

        double angle = -360.0 / (double)corners;

        for (int i = 0; i < corners; i++) {

            Point3 point = new Point3(Math.cos(Math.toRadians(angle * i)) * radius, Math.sin(Math.toRadians(angle * i)) * radius, zOffset);
            pointsList.add(point);
            objPointsList.add(point);

        }

        result.fromList(pointsList);

        return result;

    }

    public void resetSensors() {

        for (LighthouseSensor sensor: sensors) {

            sensor.sawSweep = false;

        }

    }

    public MatOfPoint2f constructImgPoints() {

        MatOfPoint2f _imgPoints = new MatOfPoint2f();

        return _imgPoints;

    }

}

package nl.brendanspijkerman.discustrajectorycalculator.models;

import android.os.AsyncTask;
import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.features2d.Params;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;

/**
 * Created by brendan on 13/01/2017.
 */

public class DataDiscusStreamReader extends AsyncTask<DataDiscusStreamReaderParameters, Void, Void> {

    private static final String TAG = "DataDiscusStreamReader";

    private InputStream inputStream;
    private volatile boolean shouldContinue = true;
    private DataDiscus dataDiscus;
    private BaseStation baseStation;

    // Header metadata length
    private int headerLength = 1;
    // Message length for each sensor. 1 Byte indicating sensor id and 4 bytes of timing data
    private int msgLength = 5;

//    public DataDiscusStreamReader(InputStream _inputStream, DataDiscus _dataDiscus) {
//
//        inputStream = _inputStream;
//
//        dataDiscus = _dataDiscus;
//        baseStation = dataDiscus.baseStation;
//        headerLength = dataDiscus.headerLength;
//        msgLength = dataDiscus.msgLength;
//
//    }

    @Override
    protected Void doInBackground(DataDiscusStreamReaderParameters... params){

        // Buffer to hold, at most, 2 integer values
        // This buffer is constantly checked to see if the start flags ({0xff, 0xff}) is present
        int[] flagBuffer = new int[2];

        // Buffer to store all incoming data.
        ArrayList<Integer> inBuffer = new ArrayList<>();

        // Read the inputStream, if it is not null
        while (inputStream != null) {

            try {

                // Write the previous value into the second index
                flagBuffer[1] = flagBuffer[0];
                // Write the new value into the first index
                flagBuffer[0] = inputStream.read();

                // Write new value into main buffer
                inBuffer.add(flagBuffer[0]);

                // Check if the flagBuffer contains the start flags
                if (flagBuffer[0] == 255 && flagBuffer[1] == 255) {

                    try {

                        // Parse the data. parseData parses the inBuffer and performs solvePnP.
                        // It writes the results into the DataDiscus class assigned to the
                        // DataDiscusStreamReader.
                        double[] pos = parseData(inBuffer);

                        double x = pos[0];
                        double y = pos[1];
                        double z = pos[2];

                        Log.i(TAG, String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z));

                    } catch (Exception e) {

//                        Log.e(TAG, e.toString());

                    }

                    // Current buffer parsed and processed. Reset buffer for next message
                    inBuffer = new ArrayList<Integer>();
                }

            } catch (Exception e) {



            }

        }

        // Void is not the same as void. With Void something still has to be returned. Typically null.
        return null;

    }

    public double[] parseData(ArrayList<Integer> data) {

        if (data.size() > 0) {

            // Reset the sensors of dataDiscus. This sets the 'sawSweep' state to 'false'
            dataDiscus.resetSensors();

            // Calculate the amount of sensors that we received data from
            int observedSensorCount = (int) Math.floor((double) (data.size() - headerLength) / (double) msgLength);

            // Get the meta byte from the buffer
            // The meta byte holds the skip, rotor and data bits.
            // The meta byte is read from right to left (least significant bit first)
            int meta = data.get(0);

            // Assign values obtained from meta byte
            baseStation.skip = (byte) getBit(meta, 2);
            baseStation.rotor = (byte) getBit(meta, 1);
            baseStation.data = (byte) getBit(meta, 0);

            // If data from one or more sensors has been received, continue
            if (observedSensorCount > 0) {

                // Create two ArrayLists to hold the imagepoints and objectpoitns of the
                // observed sensors.
                ArrayList<Point> observedImgPointsList = new ArrayList<>();
                ArrayList<Point3> observedObjPointsList = new ArrayList<>();

                // Iterate over observed sensors
                for (int i = 0; i < observedSensorCount; i++) {

                    // The read index is updated. Each sensor sends a header byte containing its id
                    // followed by 4 bytes containing the deltaT between the pulse and sweep.
                    // The deltaT is the amount of clockcycles
                    int readPos = (i * msgLength) + headerLength;

                    // Get the sensor's id
                    int sensorId = data.get(readPos);

                    // Get the virtual sensor object from the DataDiscus object.
                    LighthouseSensor _sensor = dataDiscus.sensors.get(sensorId);

                    // Update the sensor's deltaT. Still expressed as clockcycles
                    _sensor.deltaT = (((data.get(readPos + 1) & 0xFF) << 24) | ((data.get(readPos + 2) & 0xFF) << 16) | ((data.get(readPos + 3) & 0xFF) << 8) | (data.get(readPos + 4) & 0xFF));

                    // Calculate angle from deltaT
                    double angle = getAngle(_sensor.deltaT);

                    // Check if the x, or y axis rotor was sweeping
                    if (baseStation.rotor == 0) {

                        // getAngle returns -1 if the value is an outlier (outside of the basestation's fov)
                        if (angle != -1) {

                            // Update the sensor's angle and position data
                            _sensor.angles[0] = getAngle(_sensor.deltaT);
                            _sensor.position2D[0] = getScreenX(_sensor.angles[0]);

                            // A valid angle was obtained, this sensor therefore registered a valid sweep
                            // and should be included in the solvePnP function
                            _sensor.sawSweep = true;

                        }

                    } else {

                        // getAngle returns -1 if the value is an outlier (outside of the basestation's fov)
                        if (angle != -1) {

                            // Update the sensor's angle and position data
                            _sensor.angles[1] = getAngle(_sensor.deltaT);
                            _sensor.position2D[1] = getScreenY(_sensor.angles[1]);

                            // A valid angle was obtained, this sensor therefore registered a valid sweep
                            // and should be included in the solvePnP function
                            _sensor.sawSweep = true;

                        }

                    }

                    // If the sensor saw a valid sweep, include it in the imagepoints
                    if (_sensor.sawSweep) {

                        // Construct a new 2D imagepoint based on the sensor's 2D screen position
                        Point point = new Point(_sensor.position2D[0], _sensor.position2D[1]);

                        // Populate the image and object points lists with the sensors that were observed
                        observedImgPointsList.add(point);
                        observedObjPointsList.add(dataDiscus.objPointsList.get(sensorId));
                    }

//

                }

                // Construct two new matrices to hold the imagepoints and objectpoints.
                // This is purely so OpenCV can perform the solvePnP calculation
                MatOfPoint2f _imgPoints = new MatOfPoint2f();
                MatOfPoint3f _objPoints = new MatOfPoint3f();

                // Populate the new imagepoints and objectpoints matrices
                _imgPoints.fromList(observedImgPointsList);
                _objPoints.fromList(observedObjPointsList);

                // Perform solvePnP
                return solvePnP(_objPoints, _imgPoints);

            }

        }

        return null;

    }

    private double getAngle(int deltaT) {

        double angle = 0;

        double fovLimit = baseStation.fov / 2.0;

        angle = ((double) deltaT / (double) dataDiscus.SWEEP_CYCLE_CLOCK_CYCLES) * 180.0;
        angle -= 90;

//        Log.i("Solver", String.valueOf(angle));

        if (angle < -fovLimit || angle > fovLimit) {

            return -1;

        }

        return angle;
    }

    double getScreenX(double angle) {

        // Invert the angle so the X-direction matches that of the base station pov
        angle *= -1;

        double xPos;
        double max = Math.tan((Math.toRadians(baseStation.fov) / 2.0));
        double perc = Math.tan(Math.toRadians(angle)) / max;
        //println(max, perc, angle);
        xPos = baseStation.halfRes + (perc * baseStation.halfRes);

        return xPos;

    }

    double getScreenY(double angle) {

        return getScreenX(-angle);

    }

    public double[] solvePnP(MatOfPoint3f _objPoints, MatOfPoint2f _imgPoints) {

        Mat outputR = new Mat(3, 1, CvType.CV_64FC1);
        Mat outputT = new Mat(3, 1, CvType.CV_64FC1);

        try {

            Calib3d.solvePnP(_objPoints, _imgPoints, baseStation.cameraMatrix, baseStation.distortionCoefficients, outputR, outputT);

            Mat rMat = new Mat();
            Calib3d.Rodrigues(outputR, rMat);

            double[] x = new double[1];
            double[] y = new double[1];
            double[] z = new double[1];

            double[] xR = new double[1];
            double[] yR = new double[1];
            double[] zR = new double[1];

            outputT.get(0, 0, x);
            outputT.get(1, 0, y);
            outputT.get(2, 0, z);

            rMat.get(0, 0, xR);
            rMat.get(1, 0, yR);
            rMat.get(2, 0, zR);

            double[] pos = {x[0], y[0], z[0]};
            double[] rot = {xR[0], yR[0], zR[0]};

            dataDiscus.position.add(0, pos);

            //Incorrect, should be
            dataDiscus.rotation.add(0, rot);

            return pos;

        } catch (Exception e) {

            Log.e("ERROR", e.toString());

            return null;

        }

    }

    private int getBit(int number, int position) {
        return (number >> position) & 1;
    }

    private boolean getBitAsBoolean(int number, int position) {

        byte val = (byte) getBit(number, position);

        if (val == 0) {
            return false;
        } else {
            return true;
        }

    }

}

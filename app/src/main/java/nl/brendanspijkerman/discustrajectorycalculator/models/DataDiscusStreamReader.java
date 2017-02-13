package nl.brendanspijkerman.discustrajectorycalculator.models;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;

/**
 * Created by brendan on 13/01/2017.
 */

public class DataDiscusStreamReader extends Thread {

    private static final String TAG = "DataDiscusStreamReader";

    private InputStream inputStream;
    public boolean shouldContinue = true;
    private DataDiscus dataDiscus;
    private BaseStation baseStation;

    // Header metadata length
    private int headerLength = 1;
    // Start flag length
    private int startFlagLength = 2;
    // Message length for each sensor. 1 Byte indicating sensor id and 4 bytes of timing data
    private int msgLength = 5;

    public DataDiscusStreamReader(InputStream _inputStream, DataDiscus _dataDiscus) {

        inputStream = _inputStream;

        dataDiscus = _dataDiscus;
        baseStation = dataDiscus.baseStation;
        headerLength = dataDiscus.headerLength;
        startFlagLength = dataDiscus.startFlagLength;
        msgLength = dataDiscus.msgLength;

    }

    public void run() {

        int[] flagBuffer = new int[2];
        ArrayList<Integer> inBuffer = new ArrayList<>();

        while (!this.isInterrupted() && inputStream != null) {

            try {


                flagBuffer[1] = flagBuffer[0];
                flagBuffer[0] = inputStream.read();
                inBuffer.add(flagBuffer[0]);

//                                Log.i(TAG, String.valueOf(inputStream.read()));

                if (flagBuffer[0] == 255 && flagBuffer[1] == 255) {
                    //println("Startflag received");

                    try {

                        double[] pos = parseData(inBuffer);

                        double x = pos[0];
                        double y = pos[1];
                        double z = pos[2];

//                        Log.i(TAG, String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z));
                    } catch (Exception e) {
//                        Log.e(TAG, e.toString());
                    }

                    inBuffer = new ArrayList<Integer>();
                }

            } catch (Exception e) {



            }

        }

    }

    public double[] parseData(ArrayList<Integer> data) {

        if (data.size() > 0) {

            dataDiscus.resetSensors();

            int observedSensorCount = (int) Math.floor((double) (data.size() - headerLength - startFlagLength) / (double) msgLength);

            int meta = data.get(0);

            baseStation.skip = (byte) getBit(meta, 2);
            baseStation.rotor = (byte) getBit(meta, 1);
            baseStation.data = (byte) getBit(meta, 0);

            boolean shouldPerformPnPSolve = false;

            if (observedSensorCount > 0) {

                ArrayList<Point> observedImgPointsList = new ArrayList<>();
                ArrayList<Point3> observedObjPointsList = new ArrayList<>();

                for (int i = 0; i < observedSensorCount; i++) {

                    int readPos = (i * msgLength) + headerLength;
                    int sensorId = data.get(readPos);

                    LighthouseSensor _sensor = dataDiscus.sensors.get(sensorId - 2);

                    _sensor.deltaT = (((data.get(readPos + 1) & 0xFF) << 24) | ((data.get(readPos + 2) & 0xFF) << 16) | ((data.get(readPos + 3) & 0xFF) << 8) | (data.get(readPos + 4) & 0xFF));

                    double angle = getAngle(_sensor.deltaT);

                    if (baseStation.rotor == 0) {

                        if (angle != -1) {
                            _sensor.angles[0] = getAngle(_sensor.deltaT);
                            _sensor.position2D[0] = getScreenX(_sensor.angles[0]);

//                            if (_sensor.id == 2) {
//                                Log.i("Solver", "Angle: " + String.valueOf(_sensor.angles[0]));
//                                Log.i("Solver", "XPos: " + String.valueOf(_sensor.position2D[0]));
//                            }

                            _sensor.sawSweep = true;
                            shouldPerformPnPSolve = true;

                        }

                    } else {

                        if (angle != -1) {
                            _sensor.angles[1] = getAngle(_sensor.deltaT);
                            _sensor.position2D[1] = getScreenY(_sensor.angles[1]);

//                            if (_sensor.id == 2) {
//                                Log.i("Solver", "Angle: " + String.valueOf(_sensor.angles[1]));
//                                Log.i("Solver", "XPos: " + String.valueOf(_sensor.position2D[1]));
//                            }

                            _sensor.sawSweep = true;

                        }

                    }

                    if (_sensor.sawSweep) {
//                        Log.i("DataDiscus", String.valueOf(_sensor.angles[0]) + " " + String.valueOf(_sensor.angles[1]));
                        Point point = new Point(_sensor.position2D[0], _sensor.position2D[1]);
//                        Log.i(TAG, "Sensor " + String.valueOf(_sensor.id) + " has pos" + String.valueOf(_sensor.position2D[0]) + " " + String.valueOf(_sensor.position2D[1]));

                        // Populate the image and object points lists with the sensors that were observed
                        observedImgPointsList.add(point);
                        observedObjPointsList.add(dataDiscus.objPointsList.get(sensorId - 2));

                    }

                }
//                Log.i(TAG, "Starting solve");
                int b = 0;

                MatOfPoint2f _imgPoints = new MatOfPoint2f();
                MatOfPoint3f _objPoints = new MatOfPoint3f();

                _imgPoints.fromList(observedImgPointsList);
                _objPoints.fromList(observedObjPointsList);

//                Log.i("Solver", observedImgPointsList.toString());


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

        double xPos;
        double max = Math.tan((Math.toRadians(baseStation.fov) / 2.0));
        double perc = Math.tan(Math.toRadians(angle)) / max;
        //println(max, perc, angle);
        xPos = baseStation.halfRes + (perc * baseStation.halfRes);

        return xPos;

    }

    double getScreenY(double angle) {

        return getScreenX(angle);

    }

    public double[] solvePnP(MatOfPoint3f _objPoints, MatOfPoint2f _imgPoints) {

        Mat rvecs = new Mat(3, 1, CvType.CV_64FC1);
        Mat tvecs = new Mat(3, 1, CvType.CV_64FC1);

        try {

            Calib3d.solvePnP(_objPoints, _imgPoints, baseStation.cameraMatrix, baseStation.distortionCoefficients, rvecs, tvecs);
//            Calib3d.solvePnP(_objPoints, _imgPoints, baseStation.cameraMatrix, baseStation.distortionCoefficients, rvecs, tvecs, false, Calib3d.CV_EPNP);

            dataDiscus.rVecs = rvecs;

            Mat rMat = new Mat();
            Calib3d.Rodrigues(rvecs, rMat);

            // Transpose the matrix
//            rMat = rMat.t();
            dataDiscus.rotationMatrix = rMat;

            Mat viewMatrix = new Mat(4, 4, CvType.CV_64FC1);
//            Mat transferMatrix = new Mat(4, 4, CvType.CV_64FC1);
            Mat transferMatrix = new Mat(4, 4, CvType.CV_64FC1);

            for(int i = 0; i < 3; i++) {

                for(int j = 0; j < 3; j++) {

                    viewMatrix.put(i, j, rMat.get(i, j));

                }

                viewMatrix.put(i, 3, tvecs.get(i, 0));
                viewMatrix.put(3, i, 0.0f);

            }

            for(int i = 0; i < 4; i++) {

                for(int j = 0; j < 4; j++) {

                    if (i == 1 || i == 2) {

                        transferMatrix.put(i, j, -1.0f);

                    } else {

                        transferMatrix.put(i, j, 1.0f);

                    }

                }

            }

            Log.i(TAG, "View Matrix " + viewMatrix.dump().toString());
            Log.i(TAG, "Transfer Matrix " + transferMatrix.dump().toString());

            viewMatrix.put(3, 3, 1.0f);

            viewMatrix.get(0, 0, dataDiscus.rotationMatrixArray);

//            dataDiscus.rotationMatrixArray = (double)rMat.;

            double[] x = new double[1];
            double[] y = new double[1];
            double[] z = new double[1];

            double[] xR = new double[1];
            double[] yR = new double[1];
            double[] zR = new double[1];

            tvecs.get(0, 0, x);
            tvecs.get(1, 0, y);
            tvecs.get(2, 0, z);

            rMat.get(0, 0, xR);
            rMat.get(1, 0, yR);
            rMat.get(2, 0, zR);

            double[] pos = {x[0], y[0], z[0]};
            double[] rot = {xR[0], yR[0], zR[0]};

            dataDiscus.setPosition(pos);
            dataDiscus.rotations.add(0, rot);

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

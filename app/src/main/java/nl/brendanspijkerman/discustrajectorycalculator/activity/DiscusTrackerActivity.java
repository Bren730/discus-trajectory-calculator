package nl.brendanspijkerman.discustrajectorycalculator.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;

import java.util.ArrayList;
import java.util.List;

import nl.brendanspijkerman.discustrajectorycalculator.R;

public class DiscusTrackerActivity extends AppCompatActivity {

    private static final String TAG = "OCVSample::Activity";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    int b = 4;
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discus_tracker);

        setTitle("Discus Tracker");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            try {
                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, DiscusTrackerActivity.this, mLoaderCallback);
                int b = 0;
            } catch (Exception e) {

                int b = 3;
            }

        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void openCvTestClick(View view) {

        int b = 0;

        List<Point3> pointsList = new ArrayList<>();

        Point3 point = new Point3(1, 2, 3);
        pointsList.add(point);

        MatOfPoint3f objPoints = new MatOfPoint3f();
        objPoints.fromList(pointsList);

        double[][] imgPoints = {{200, 200}, {300, 300}};

        org.opencv.core.Mat outputR = null;
        org.opencv.core.Mat outputT = null;

//        org.opencv.calib3d.Calib3d.solvePnP(objPoints, imgPoints, null, null, outputR, outputT);
    }
}

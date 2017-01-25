package nl.brendanspijkerman.discustrajectorycalculator.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import nl.brendanspijkerman.discustrajectorycalculator.R;
import nl.brendanspijkerman.discustrajectorycalculator.Renderer;
import nl.brendanspijkerman.discustrajectorycalculator.models.BaseStation;
import nl.brendanspijkerman.discustrajectorycalculator.models.DataDiscus;
import nl.brendanspijkerman.discustrajectorycalculator.models.DataDiscusStreamReader;

import static org.opencv.core.CvType.CV_64FC1;

public class DiscusTrackerActivity extends AppCompatActivity {

    private static final String TAG = "DiscusTrackerActivity";
    private static final Integer REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BaseStation baseStation;
    private DataDiscus dataDiscus;
    private InputStream inputStream;
    private DataDiscusStreamReader dataDiscusStreamReader;

    ArrayList<BluetoothDevice> BTDeviceList = new ArrayList<>();

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    TextView mPositionTextView;
    TextView mSpeedTextView;
    ProgressBar mProgressBar;
    TextView mProgressBarTextView;

    Renderer renderer;
    boolean sceneLoaded = false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // OpenCV loaded, BaseStation and DataDiscus classes can now be constructed
                    baseStation = new BaseStation(120);
                    dataDiscus = new DataDiscus(10, 0.07753, 0.00667, baseStation);
//                    dataDiscusStreamReader = new DataDiscusStreamReader(inputStream, dataDiscus);

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private void load3DScene() {

        final RajawaliSurfaceView surface = (RajawaliSurfaceView) findViewById(R.id.rajwali_surface);
//        surface.setFrameRate(60.0);
//        surface.setRenderMode(IRajawaliSurface.RENDERMODE_CONTINUOUSLY);
//        addContentView(surface, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));

        renderer = new Renderer(this);
        surface.setSurfaceRenderer(renderer);
        sceneLoaded = true;


        final int delay = 1000;

        // Update the UI TextViews every X milliseconds
        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            private long time = 0;

            @Override
            public void run()
            {

                startDeviceDiscovery();

            }
        }, delay); // 1 second delay (takes millis)

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discus_tracker);

        load3DScene();

        setTitle("Discus Tracker");

        mPositionTextView = (TextView) findViewById(R.id.discus_position);
        mSpeedTextView = (TextView) findViewById(R.id.discus_speed);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBarTextView = (TextView) findViewById(R.id.progress_bar_textview);

        // First request user permission to use coarse location
        // This is needed to scan for Bluetooth devices
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (mBluetoothAdapter == null) {
            Log.i(TAG, "This device does not support BlueTooth");
        } else {

            Log.i(TAG, "This device supports BlueTooth, checking if it is enabled...");
            if (!mBluetoothAdapter.isEnabled()) {
                Log.i(TAG, "Bluetooth not enabled. Requesting user to enable it.");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.i(TAG, "Bluetooth enabled");
            }
        }

        final int delay = 16;

        // Update the UI TextViews every X milliseconds
        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            private long time = 0;

            @Override
            public void run()
            {

                try {

                    int b = 0;

                    String x = String.format("%.2f", dataDiscus.positions.get(0).coordinates[0]);
                    String y = String.format("%.2f", dataDiscus.positions.get(0).coordinates[1]);
                    String z = String.format("%.2f", dataDiscus.positions.get(0).coordinates[2]);

                    String speed = String.format("Speed: %.2f m/s, top speed: %.2f", dataDiscus.speed, dataDiscus.topSpeed);
                    mSpeedTextView.setText(speed);

                    double xPos = dataDiscus.positions.get(0).coordinates[0] * 1000.0;
                    double yPos = dataDiscus.positions.get(0).coordinates[1] * 1000.0;
                    double zPos = dataDiscus.positions.get(0).coordinates[2] * 1000.0;

                    double[] xR = new double[1];
                    double[] yR = new double[1];
                    double[] zR = new double[1];
                    double[] rot = new double[1];

                    dataDiscus.rVecs.get(0, 0, xR);
                    dataDiscus.rVecs.get(1, 0, yR);
                    dataDiscus.rVecs.get(2, 0, zR);
                    dataDiscus.rVecs.get(0, 1, rot);

                    double xRot = (xR[0] / Math.PI) * 180;
                    double yRot = (yR[0] / Math.PI) * 180;
                    double zRot = (zR[0] / Math.PI) * 180;

//                    Log.i(TAG, String.valueOf(xR[0]) + ", " + String.valueOf(yR[0]) + ", " + String.valueOf(zR[0]) + ", " + String.valueOf(rot[0]) );

                    Quaternion quaternion = new Quaternion();
                    Matrix4 mat = new Matrix4(dataDiscus.rotationMatrixArray);
                    quaternion.fromMatrix(mat);

                    if(sceneLoaded) {

                        try {
                            renderer.discus.setPosition(xPos, yPos, zPos);
//                            renderer.discus.setRotation(xRot, yRot, zRot);

                        } catch (Exception e) {
                            Log.e("RendererInActivity", e.toString());
                        }
                    }

                    String data = "Position: x %s, y %s, z %s";
                    String output = String.format(data, x, y, z);

                    mPositionTextView.setText(output);
                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
                }

                time += delay;
                h.postDelayed(this, delay);
            }
        }, delay); // 1 second delay (takes millis)

    }

    private void checkPairedDevices() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, deviceHardwareAddress + " is currently paired");
            }
        } else {

            Log.i(TAG, "No devices currently paired, initiating discovery");
            startDeviceDiscovery();

        }
    }

    private void startDeviceDiscovery() {

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if(mBluetoothAdapter.startDiscovery()) {
            Log.i(TAG, "Starting device discovery");
        }

        //let's make a broadcast receiver to register our things
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, ifilter);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.i(TAG, "Found " + deviceName + " " + deviceHardwareAddress);
                BTDeviceList.add(device);

                try {
                    if (deviceName.equals("ArcReactor")) {

                        Log.i(TAG, "Found Data Discus");

                        mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorGreen), PorterDuff.Mode.MULTIPLY);
                        mProgressBarTextView.setText("Found Data Discus, connecting...");

                        BluetoothDevice dataDiscusDevice = mBluetoothAdapter.getRemoteDevice(deviceHardwareAddress);

                        Method m = dataDiscusDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                        BluetoothSocket socket = (BluetoothSocket) m.invoke(dataDiscusDevice, Integer.valueOf(1));
                        socket.connect();

//                        mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorGreen), PorterDuff.Mode.MULTIPLY);
                        mProgressBarTextView.setText("Connected");

                        final int delay = 4000;

                        // Update the UI TextViews every X milliseconds
                        final Handler h = new Handler();
                        h.postDelayed(new Runnable()
                        {
                            private long time = 0;

                            @Override
                            public void run()
                            {

                                mProgressBar.setVisibility(View.GONE);
                                mProgressBarTextView.setVisibility(View.GONE);

                            }
                        }, delay); // 1 second delay (takes millis)

                        try {

                            DataDiscusStreamReader dataDiscusStreamReader = new DataDiscusStreamReader(socket.getInputStream(), dataDiscus);
                            dataDiscusStreamReader.start();

                        } catch (Exception e) {

                            Log.e(TAG, e.toString());

                        }
                        mBluetoothAdapter.cancelDiscovery();
//                    BluetoothServerSocket socket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(uuid);


                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());

                }
            }
        }
    };

    public void solvePnPTest() {
        ArrayList<Point> imgPointsList = new ArrayList<>();

        imgPointsList.add(new Point(500, 500));
        imgPointsList.add(new Point(600, 600));
        imgPointsList.add(new Point(450, 600));
        imgPointsList.add(new Point(600, 450));
        imgPointsList.add(new Point(500, 800));

        MatOfPoint2f imgPoints = new MatOfPoint2f();
        imgPoints.fromList(imgPointsList);

        ArrayList<Point3> objPointsList = new ArrayList<>();

        objPointsList.add(dataDiscus.objPointsList.get(0));
        objPointsList.add(dataDiscus.objPointsList.get(1));
        objPointsList.add(dataDiscus.objPointsList.get(2));
        objPointsList.add(dataDiscus.objPointsList.get(3));
        objPointsList.add(dataDiscus.objPointsList.get(4));

        MatOfPoint3f objPoints = new MatOfPoint3f();
        objPoints.fromList(objPointsList);

    }

    @Override
    protected void onResume() {
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {

                Log.i(TAG, "User has enabled Bluetooth");

            } else {

                Log.i(TAG, "User did not enable Bluetooth, notify and quit activity");

            }

        }

    }

}

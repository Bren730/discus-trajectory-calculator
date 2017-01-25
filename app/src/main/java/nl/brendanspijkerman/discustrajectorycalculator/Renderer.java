package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;

/**
 * Created by Brendan on 25-1-2017.
 */

public class Renderer extends RajawaliRenderer {

    private static final String TAG = "Renderer";

    private DirectionalLight directionalLight;
    private Sphere sphere;
    public Object3D discus;
    private Camera mCamera;

    public Context context;

    public Renderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene() {

        mCamera = getCurrentCamera();
        mCamera.setPosition(0, 0, 0);
        mCamera.setLookAt(0, 0, 1);
        mCamera.setFieldOfView(120);
        mCamera.setFarPlane(5000);

        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(Color.GREEN);

        LoaderOBJ loaderOBJ = new LoaderOBJ(context.getResources(), mTextureManager, R.raw.discus_low_poly_obj);

        try {
            loaderOBJ.parse();
            discus = loaderOBJ.getParsedObject();
            discus.setMaterial(material);

            for (int i = 0; i < discus.getNumObjects(); i++) {
                discus.getChildAt(i).setMaterial(material);
            }

            discus.setMaterial(material);

            discus.setPosition(0, 0, 1000);
            discus.setScale(3);
            getCurrentScene().addChild(discus);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

//        sphere = new Sphere(180/2, 24, 24);
//        sphere.setPosition(0, 0, 300);
//        sphere.setMaterial(material);

//        getCurrentScene().addChild(sphere);

    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
//        discus.rotate(Vector3.Axis.Y, 1.0);

//        discus.setPosition(0, 0, discus.getPosition().z - 1);

    }

    public void onTouchEvent(MotionEvent event){
    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){
    }
}

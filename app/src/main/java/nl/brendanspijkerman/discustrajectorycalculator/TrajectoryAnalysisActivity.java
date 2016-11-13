package nl.brendanspijkerman.discustrajectorycalculator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;

import static java.security.AccessController.getContext;

public class TrajectoryAnalysisActivity extends AppCompatActivity {

    Variables variables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory_analysis);

        setTitle(R.string.trajectory_analysis);

        //Retrieve the variables object sent with the intent
        variables = (Variables) getIntent().getSerializableExtra("variables");

        AirResistanceModel model = new AirResistanceModel(variables);
        Trajectory trajectory = model.calculateTrajectory();

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.trajectory_graph);

        // Populate TextViews with the most important measurements
        TextView tv = (TextView) findViewById(R.id.final_distance);
        tv.setText(String.valueOf( Math.round(trajectory.finalDistance * 100.0) / 100.0 ));

        tv = (TextView) findViewById(R.id.release_speed);
        tv.setText(String.valueOf( Math.round(trajectory.variables.v0 * 100.0) / 100.0 ));

        tv = (TextView) findViewById(R.id.release_angle);
        tv.setText(String.valueOf( Math.round(trajectory.variables.thetaRelease0 * 100.0) / 100.0 ));

        tv = (TextView) findViewById(R.id.angle_of_attack);
        tv.setText(String.valueOf( Math.round(trajectory.variables.thetaAttack0 * 100.0) / 100.0 ));

        // Get the display's width in pixels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

//        Graph graph = new Graph(this, size.x, trajectory, (ImageView)findViewById(R.id.graph));

        SeekBar v0, theta0, thetaAttack0, vWind, g, y0, deltaT, rho, m, discusD, discusH;

        v0 = (SeekBar) findViewById(R.id.v0);
        theta0 = (SeekBar) findViewById(R.id.theta0);
        thetaAttack0 = (SeekBar) findViewById(R.id.thetaAttack);
        vWind = (SeekBar) findViewById(R.id.vWind);
        g = (SeekBar) findViewById(R.id.g);
        y0 = (SeekBar) findViewById(R.id.y0);
        deltaT = (SeekBar) findViewById(R.id.deltaT);
        rho = (SeekBar) findViewById(R.id.rho);
        m = (SeekBar) findViewById(R.id.m);
        discusD = (SeekBar) findViewById(R.id.discusD);
        discusH = (SeekBar) findViewById(R.id.discusH);

        v0.setOnSeekBarChangeListener(new eventHandler());
        theta0.setOnSeekBarChangeListener(new eventHandler());
        thetaAttack0.setOnSeekBarChangeListener(new eventHandler());
        vWind.setOnSeekBarChangeListener(new eventHandler());
        g.setOnSeekBarChangeListener(new eventHandler());
        y0.setOnSeekBarChangeListener(new eventHandler());
        deltaT.setOnSeekBarChangeListener(new eventHandler());
        rho.setOnSeekBarChangeListener(new eventHandler());
        m.setOnSeekBarChangeListener(new eventHandler());
        discusD.setOnSeekBarChangeListener(new eventHandler());
        discusH.setOnSeekBarChangeListener(new eventHandler());

    }

    private class eventHandler implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            String tag = (String) seekBar.getTag();

            try {
                Field field = Variables.class.getDeclaredField(tag);
                try {
                    field.set(variables, progress);
                    double t = variables.v0;

                    AirResistanceModel model = new AirResistanceModel(variables);
                    Trajectory trajectory = model.calculateTrajectory();

                    // Get the display's width in pixels
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    Graph graph = new Graph(seekBar.getContext(), size.x, trajectory, (ImageView)findViewById(R.id.graph));

                    // Populate TextViews with the most important measurements
                    TextView tv = (TextView) findViewById(R.id.final_distance);
                    tv.setText(String.valueOf( Math.round(trajectory.finalDistance * 100.0) / 100.0 ));

                    tv = (TextView) findViewById(R.id.release_speed);
                    tv.setText(String.valueOf( Math.round(trajectory.variables.v0 * 100.0) / 100.0 ));

                    tv = (TextView) findViewById(R.id.release_angle);
                    tv.setText(String.valueOf( Math.round(trajectory.variables.thetaRelease0 * 100.0) / 100.0 ));

                    tv = (TextView) findViewById(R.id.angle_of_attack);
                    tv.setText(String.valueOf( Math.round(trajectory.variables.thetaAttack0 * 100.0) / 100.0 ));

                } catch (IllegalAccessException iae) {
                    throw new Error("No access to " + tag + " in Variables class");
                }

            } catch (NoSuchFieldException nsfe) {
                throw new RuntimeException("No field named " + tag + " found in Variables class");
            }




        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

}
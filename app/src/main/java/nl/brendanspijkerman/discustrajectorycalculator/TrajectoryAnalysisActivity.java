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
import android.widget.TextView;

import static java.security.AccessController.getContext;

public class TrajectoryAnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory_analysis);

        setTitle(R.string.trajectory_analysis);

        //Retrieve the variables object sent with the intent
        Variables variables = (Variables) getIntent().getSerializableExtra("variables");

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

        Graph graph = new Graph(this, size.x, trajectory, (ImageView)findViewById(R.id.graph));

    }

}
package nl.brendanspijkerman.discustrajectorycalculator;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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

        TextView tv = (TextView) findViewById(R.id.final_distance);
        tv.setText(String.valueOf( Math.round(trajectory.finalDistance * 100.0) / 100.0 ));

        tv = (TextView) findViewById(R.id.release_speed);
        tv.setText(String.valueOf( Math.round(trajectory.variables.v0 * 100.0) / 100.0 ));

        tv = (TextView) findViewById(R.id.release_angle);
        tv.setText(String.valueOf( Math.round(trajectory.variables.thetaRelease0 * 100.0) / 100.0 ));

        tv = (TextView) findViewById(R.id.angle_of_attack);
        tv.setText(String.valueOf( Math.round(trajectory.variables.thetaAttack0 * 100.0) / 100.0 ));

    }

}

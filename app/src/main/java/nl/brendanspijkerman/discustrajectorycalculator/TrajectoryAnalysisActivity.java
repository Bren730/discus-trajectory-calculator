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
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        ImageView view = (ImageView) findViewById(R.id.graph);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        double scale = width / trajectory.xMax;
        int height = (int)(trajectory.yMax * scale);

        view.setMaxWidth(width);
        view.setMaxHeight(height);

        view.setMaxHeight(height);

        Path path = new Path();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);

        ShapeDrawable graph = new ShapeDrawable(new PathShape(path, width, height));
        graph.getPaint().set(paint);

        path.moveTo((float)trajectory.data.get(0).x, (float)trajectory.data.get(0).y);

        for (int i = 0; i < trajectory.data.size() - 1; i++) {

            path.lineTo((float)(trajectory.data.get(i + 1).x * scale), (float)(height - (trajectory.data.get(i + 1).y * scale)));

        }

        view.setBackground(graph);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

    }

}
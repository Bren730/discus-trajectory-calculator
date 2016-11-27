package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void newTrajectoryAnalyzer(View view) {

        Intent intent = new Intent(this, TrajectoryAnalysisActivity.class);
        Variables variables = new Variables(20, 35, 0, 1.8);
        intent.putExtra("variables", variables);
        startActivity(intent);

    }

    public void newAthlete(View view) {

        Intent intent = new Intent(this, NewAthleteActivity.class);
//        Variables variables = new Variables(20, 35, 0, 1.8);
//        intent.putExtra("variables", variables);
        startActivity(intent);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

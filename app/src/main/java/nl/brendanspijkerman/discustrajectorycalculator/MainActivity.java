package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import nl.brendanspijkerman.discustrajectorycalculator.activity.DiscusTrackerActivity;
import nl.brendanspijkerman.discustrajectorycalculator.adapter.AthletesAdapter;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private Storage storage = new Storage(this);
    private Athletes athletes;
    private RecyclerView recyclerView;
    private AthletesAdapter mAthletesAdapter;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        try {
            athletes = storage.loadAthletes();

            mAthletesAdapter = new AthletesAdapter(athletes.entries);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAthletesAdapter);

            mAthletesAdapter.notifyDataSetChanged();
        } catch (Exception e) {

            int b = 0;

        }

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

    public void newTraining(View view) {

        Intent intent = new Intent(this, DiscusTrackerActivity.class);
        startActivity(intent);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import nl.brendanspijkerman.discustrajectorycalculator.activities.DiscusTrackerActivity;
import nl.brendanspijkerman.discustrajectorycalculator.adapters.AthletesAdapter;

public class MainActivity extends AppCompatActivity {

    private Storage storage = new Storage(this);
    private Athletes athletes;
    private RecyclerView recyclerView;
    private AthletesAdapter mAthletesAdapter;

    static final int NEW_ATHLETE_REQUEST = 1;

    private static final String TAG = "MainActivity";

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

            mAthletesAdapter = new AthletesAdapter(this, athletes.entries);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());

            SlideInLeftAnimator animator = new SlideInLeftAnimator();
            animator.setInterpolator(new OvershootInterpolator());
            animator.setAddDuration(500);

            recyclerView.setItemAnimator(animator);

            int adapterAnimLength = 300;

            AlphaInAnimationAdapter adapterAnimation = new AlphaInAnimationAdapter(mAthletesAdapter);
            adapterAnimation.setDuration(adapterAnimLength);

            recyclerView.setAdapter(adapterAnimation);

            mAthletesAdapter.notifyDataSetChanged();
        } catch (Exception e) {

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
        startActivityForResult(intent, NEW_ATHLETE_REQUEST);

    }

    public void newTraining(View view) {

        Intent intent = new Intent(this, DiscusTrackerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_ATHLETE_REQUEST) {

            try {

                String athleteId = data.getStringExtra("athleteId");

                Storage storage = new Storage(this);
                Athlete athlete = storage.loadAthlete(UUID.fromString(athleteId));

                int gh = 1;

                if (athlete != null) {

                    athletes.addAthlete(athlete);

                    // Update UI after X time
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAthletesAdapter.notifyItemInserted(0);
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }, 300);


                }
            } catch (Exception e) {

            }

            if (resultCode == RESULT_OK) {

                int b = 0;

            }
        }

    }

}

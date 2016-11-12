package nl.brendanspijkerman.discustrajectorycalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TrajectoryAnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory_analysis);

        setTitle(R.string.trajectory_analysis);

        //Retrieve the variables object sent with the intent
        Variables variables = (Variables) getIntent().getSerializableExtra("variables");

        AirResistanceModel model = new AirResistanceModel(variables);
        model.calculate();

    }

}

package nl.brendanspijkerman.discustrajectorycalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by Brendan on 13-11-2016.
 */

public class Graph {

    int lineThickness;
    int xAxisMargin;
    int yAxisMargin;

    Graph (Context context, int width, ArrayList<Trajectory> trajectories, LineChart view){

        view.setDescription(null);
        view.setBorderColor(context.getResources().getColor(R.color.colorPrimary));
        view.getAxisRight().setDrawLabels(false);
        view.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // Styling y-axis
        view.getAxisLeft().setAxisLineColor(context.getResources().getColor(R.color.colorAccent));
        view.getAxisLeft().setGridColor(context.getResources().getColor(R.color.colorAccent));
        view.getAxisLeft().setDrawGridLines(true);
        view.getAxisLeft().setTextColor(context.getResources().getColor(R.color.colorAccent));
        view.getAxisLeft().setAxisMinimum(0);
        view.getAxisLeft().setAxisMaximum(20);
        view.getAxisLeft().setLabelCount(4);

        // Styling x-axis
        view.getXAxis().setGridColor(context.getResources().getColor(R.color.colorAccent));
        view.getXAxis().setAxisLineColor(context.getResources().getColor(R.color.colorAccent));
        view.getXAxis().setTextColor(context.getResources().getColor(R.color.colorAccent));
        view.getXAxis().setAxisMinimum(0);
        view.getXAxis().setAxisMaximum(90);
        view.getXAxis().setLabelCount(10);

        LineData lineData = new LineData();

        int index = 0;

        for (Trajectory trajectory : trajectories){

            ArrayList<Entry> entries = new ArrayList<>();

            for (Telemetry telemetry : trajectory.data) {

                entries.add(new Entry((float)telemetry.x, (float)telemetry.y));

            }

            LineDataSet dataSet = new LineDataSet(entries, "Throw " + (index + 1));
            dataSet.setMode(LineDataSet.Mode.LINEAR);

            if (index == 0) {
                dataSet.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                dataSet.setColor(context.getResources().getColor(R.color.colorAccent));
            }

            dataSet.setLineWidth(2);

            dataSet.setDrawCircles(false);
//            dataSet.setCircleRadius(1);
//            dataSet.setCircleColor(context.getResources().getColor(R.color.colorPrimary));
//            dataSet.setDrawCircleHole(false);

            dataSet.setDrawValues(false);
            lineData.addDataSet(dataSet);

            index++;

        }

        view.setData(lineData);
        //view.animateX((int)(trajectories.get(0).flightTime * 1000), Easing.EasingOption.Linear);
        view.invalidate();

    }

    int dpToPx(double dp, DisplayMetrics metrics) {

        int px = (int)Math.round(dp* (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return px;

    }

}

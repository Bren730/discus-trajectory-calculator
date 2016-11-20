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

        // Color styling y-axis
        view.getAxisLeft().setAxisLineColor(context.getResources().getColor(R.color.colorAccent));
        view.getAxisLeft().setGridColor(context.getResources().getColor(R.color.colorAccent));
        view.getAxisLeft().setTextColor(context.getResources().getColor(R.color.colorAccent));

        // Color styling x-axis
        view.getXAxis().setGridColor(context.getResources().getColor(R.color.colorAccent));
        view.getXAxis().setAxisLineColor(context.getResources().getColor(R.color.colorAccent));
        view.getXAxis().setTextColor(context.getResources().getColor(R.color.colorAccent));

        LineData lineData = new LineData();

        int index = 0;

        for (Trajectory trajectory : trajectories){

            ArrayList<Entry> entries = new ArrayList<>();

            for (Telemetry telemetry : trajectory.data) {

                entries.add(new Entry((float)telemetry.x, (float)telemetry.y));

            }

            LineDataSet dataSet = new LineDataSet(entries, "Throw " + (index + 1));

            if (index == 0) {
                dataSet.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                dataSet.setColor(context.getResources().getColor(R.color.colorAccent));
            }

            dataSet.setLineWidth(2);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            lineData.addDataSet(dataSet);

            index++;

        }

        view.setData(lineData);
        view.invalidate();

//        DisplayMetrics metrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
//
//        lineThickness = dpToPx(1, metrics);
//        xAxisMargin = dpToPx(30, metrics);
//        yAxisMargin = dpToPx(30, metrics);
//
//        // Get the display's width in pixels
//        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Point size = new Point();
//        display.getSize(size);
//
//        // Determine the scale of the graph in px/m
//        double scale = (width - xAxisMargin) / trajectory.xMax;
//        // Add stroke width to height to avoid clipping the top of the trajectory
//        int height = (int)(trajectory.yMax * scale + lineThickness + yAxisMargin);
//
//        // Set the height of the ImageView element
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
//        params.width = width;
//        params.height = height;
//        view.setLayoutParams(params);
//
//        // Create the stroke style
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(context.getResources().getColor(R.color.colorAccent));
//        paint.setStrokeWidth(lineThickness);
//
//        // Create a new path and set the first point to the first point stored in trajectory.data
//        Path discusPath = new Path();
//
//        discusPath.moveTo((float)(trajectory.data.get(0).x * scale + xAxisMargin), (float)(height - (trajectory.data.get(0).y * scale) - yAxisMargin));
//
//        // Create a new ShapeDrawable that will be set as the background of the ImageView
//        ShapeDrawable graph = new ShapeDrawable(new PathShape(discusPath, width, height));
//        graph.getPaint().set(paint);
//
//        // For each point in trajectory.data, draw a line
//        for (int i = 0; i < trajectory.data.size() - 1; i++) {
//
//            discusPath.lineTo((float)(trajectory.data.get(i + 1).x * scale + xAxisMargin), (float)(height - (trajectory.data.get(i + 1).y * scale) - yAxisMargin));
//
//        }
//
//        // Create the stroke style
//        Paint colorPrimaryPaint = new Paint();
//        colorPrimaryPaint.setStyle(Paint.Style.STROKE);
//        colorPrimaryPaint.setColor(context.getResources().getColor(R.color.colorPrimary));
//        colorPrimaryPaint.setStrokeWidth(lineThickness);
//
////        graph.getPaint().set(colorPrimaryPaint);
//        Path axes = new Path();
//        axes.moveTo(xAxisMargin, 0);
//        ShapeDrawable axesDrawable = new ShapeDrawable(new PathShape(axes, width, height));
//        axes.lineTo(xAxisMargin, height - yAxisMargin);
//        axes.lineTo(width, height - yAxisMargin);
//
//        axesDrawable.getPaint().set(colorPrimaryPaint);
//
//        Bitmap combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(combined);
//
//        graph.draw(canvas);
//        canvas.drawBitmap(output, 0, 0, null);
//
////        ImageView view2 = new ImageView(context);
//
//        // Set the ShapeDrawable as the background of the ImageView
//        view.setImageBitmap(output);

    }

    int dpToPx(double dp, DisplayMetrics metrics) {

        int px = (int)Math.round(dp* (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return px;

    }
}

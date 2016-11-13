package nl.brendanspijkerman.discustrajectorycalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Brendan on 13-11-2016.
 */

public class Graph {

    Graph (Context context, int width, Trajectory trajectory, View view){

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();

        // Get the display's width in pixels
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        display.getSize(size);

        // Convert 1dp to a pixel value so the line thickness is consistent across devices
        int px = Math.round(1 * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        // Determine the scale of the graph in px/m
        double scale = width / trajectory.xMax;
        // Add stroke width to height to avoid clipping the top of the trajectory
        int height = (int)(trajectory.yMax * scale + px);

        // Set the height of the ImageView element
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        // Create a new path and set the first point to the first point stored in trajectory.data
        Path path = new Path();
        path.moveTo((float)(trajectory.data.get(0).x * scale), (float)(height - (trajectory.data.get(0).y * scale)));

        // Create the stroke style
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 241, 94, 34));
        paint.setStrokeWidth(px);

        // Create a new ShapeDrawable that will be set as the background of the ImageView
        ShapeDrawable graph = new ShapeDrawable(new PathShape(path, width, height));
        graph.getPaint().set(paint);

        // For each point in trajectory.data, draw a line
        for (int i = 0; i < trajectory.data.size() - 1; i++) {

            path.lineTo((float)(trajectory.data.get(i + 1).x * scale), (float)(height - (trajectory.data.get(i + 1).y * scale)));

        }

        // Set the ShapeDrawable as the background of the ImageView
        view.setBackground(graph);

    }
}

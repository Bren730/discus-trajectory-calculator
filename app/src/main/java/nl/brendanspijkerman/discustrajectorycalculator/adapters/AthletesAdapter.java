package nl.brendanspijkerman.discustrajectorycalculator.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import nl.brendanspijkerman.discustrajectorycalculator.Athlete;
import nl.brendanspijkerman.discustrajectorycalculator.R;
import nl.brendanspijkerman.discustrajectorycalculator.activities.DiscusTrackerActivity;


/**
 * Created by Brendan on 12-12-2016.
 */

public class AthletesAdapter extends RecyclerView.Adapter<AthletesAdapter.MyViewHolder> {

    private Context mContext;

    private int lastPosition = -1;

    private List<Athlete> athletesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        FrameLayout container;

        public TextView athleteName;
        public ImageView athletePhoto;
        public Button newTrainingBtn;

        public MyViewHolder(View view) {
            super(view);
            container = (FrameLayout) view.findViewById(R.id.recycler_view);
            athleteName = (TextView) view.findViewById(R.id.athlete_name);
            athletePhoto = (ImageView) view.findViewById(R.id.athlete_photo);
            newTrainingBtn = (Button) view.findViewById(R.id.athlete_new_training);
        }
    }


    public AthletesAdapter(Context _context, List<Athlete> athletesList) {

        this.mContext = _context;
        this.athletesList = athletesList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.athlete_list_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Athlete athlete = athletesList.get(position);
        holder.athleteName.setText(athlete.getName());

        BitmapWorkerTask task = new BitmapWorkerTask(holder.athletePhoto);
        task.execute(athlete);

        holder.newTrainingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int b = 0;
                Intent intent = new Intent(mContext, DiscusTrackerActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }

    class BitmapWorkerTask extends AsyncTask<Athlete, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Athlete... params) {
//            Bitmap bmp = BitmapFactory.decodeFile(params[0].photoUri.getPath());
//            return Bitmap.createScaledBitmap(bmp, 400, 400, false);

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(params[0].photoUri), null, bitmapOptions);
            } catch (Exception e) {

            }

            bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions, 150, 150);
            bitmapOptions.inJustDecodeBounds = false;

            try {

                InputStream inputStream = mContext.getContentResolver().openInputStream(params[0].photoUri);
                Bitmap scaledBitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
                return scaledBitmap;

            } catch (Exception e) {

            }

            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return athletesList.size();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}

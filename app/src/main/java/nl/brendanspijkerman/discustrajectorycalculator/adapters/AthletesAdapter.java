package nl.brendanspijkerman.discustrajectorycalculator.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import nl.brendanspijkerman.discustrajectorycalculator.Athlete;
import nl.brendanspijkerman.discustrajectorycalculator.R;


/**
 * Created by Brendan on 12-12-2016.
 */

public class AthletesAdapter extends RecyclerView.Adapter<AthletesAdapter.MyViewHolder> {

    private Context mContext;

    private int lastPosition = -1;

    AthletesAdapter(Context _context) {

        mContext = _context;

    }

    private List<Athlete> athletesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        FrameLayout container;

        public TextView athleteName;
        public ImageView athletePhoto;

        public MyViewHolder(View view) {
            super(view);
            container = (FrameLayout) view.findViewById(R.id.recycler_view);
            athleteName = (TextView) view.findViewById(R.id.athlete_name);
            athletePhoto = (ImageView) view.findViewById(R.id.athlete_photo);
        }
    }


    public AthletesAdapter(List<Athlete> athletesList) {

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

//        Bitmap bmp = BitmapFactory.decodeFile(athlete.photoUri.getPath());
//        holder.athletePhoto.setImageBitmap(Bitmap.createScaledBitmap(bmp, 400, 400, false));
//        bmp.recycle();

//        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//        bitmapOptions.inJustDecodeBounds = true;
//        try {
//            BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(athlete.photoUri), null, bitmapOptions);
//        } catch (Exception e) {
//
//        }
//
//        bitmapOptions.inSampleSize = calculateInSampleSize(bitmapOptions, 100, 100);
//        bitmapOptions.inJustDecodeBounds = false;
//
//        try {
//
//            InputStream inputStream = mContext.getContentResolver().openInputStream(athlete.photoUri);
//            Bitmap scaledBitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
//            holder.athletePhoto.setImageBitmap(scaledBitmap);
//
//        } catch (Exception e) {
//
//        }

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
            Bitmap bmp = BitmapFactory.decodeFile(params[0].photoUri.getPath());
            return Bitmap.createScaledBitmap(bmp, 400, 400, false);
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

    private void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }

}

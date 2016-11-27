package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewAthleteActivity extends AppCompatActivity {

    private Uri mImageUri;
    private static Context context;
    String mCurrentPhotoPath = null;
    String mPreviousPhotoPath = null;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_athlete);
        NewAthleteActivity.context = getApplicationContext();

        setTitle(R.string.title_new_athlete);

    }

    public void startCamera(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "nl.brendanspijkerman.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    private File createImageFile() throws IOException {

        // Store the previous photo path so we can delete the file if a new picture is taken
        if (mCurrentPhotoPath != null) {

            mPreviousPhotoPath = mCurrentPhotoPath;

        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("temp/pictures");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        File f = new File(mCurrentPhotoPath);
        Uri fileUri = Uri.fromFile(f);
        ImageView view = (ImageView) findViewById(R.id.new_athlete_picture);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setImageURI(fileUri);

    }

    public void clickNext(View view) {

        if (checkFields()) {

            ImageView imgView = (ImageView) findViewById(R.id.new_athlete_picture);
            EditText name = (EditText) findViewById(R.id.new_athlete_name);
            EditText height = (EditText) findViewById(R.id.new_athlete_height);
            Spinner throwingClass = (Spinner) findViewById(R.id.new_athlete_class);

            Athlete athlete = new Athlete(name.getText().toString(), Float.parseFloat(height.getText().toString()), throwingClass.getSelectedItem().toString());
            Storage storage = new Storage(this);
            storage.saveAthlete(athlete);

        }

    }

    public boolean checkFields() {

        ImageView imgView = (ImageView) findViewById(R.id.new_athlete_picture);
        EditText name = (EditText) findViewById(R.id.new_athlete_name);
        EditText height = (EditText) findViewById(R.id.new_athlete_height);
        Spinner throwingClass = (Spinner) findViewById(R.id.new_athlete_class);

        String _name = name.getText().toString();
        float _height = 0;

        try {

            _height = Float.parseFloat(height.getText().toString());

        } catch (Exception e) {

            _height = 0;

        }

        String _throwingClass = throwingClass.getSelectedItem().toString();

        if (!_name.equals("") && _height > 0 && !_throwingClass.equals("")) {

            return true;

        }

        return false;

    }

}

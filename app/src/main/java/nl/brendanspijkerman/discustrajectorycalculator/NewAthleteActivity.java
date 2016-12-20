package nl.brendanspijkerman.discustrajectorycalculator;

import android.app.DatePickerDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.brendanspijkerman.discustrajectorycalculator.model.Sex;

public class NewAthleteActivity extends AppCompatActivity {

    private Uri mImageUri;
    private static Context context;
    String mCurrentPhotoPath = null;
    String mPreviousPhotoPath = null;

    private String _name = null;
    private Calendar _birthday = null;
    private Sex _sex = null;
    private Uri _photoUri = null;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_athlete);
        NewAthleteActivity.context = getApplicationContext();

        EditText editText = (EditText) findViewById(R.id.new_athlete_name);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.new_athlete_sex);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                _name = s.toString();

                int d = 9;
            }

        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                switch (checkedRadioButton.getText().toString()) {

                    case "Male":
                        _sex = Sex.MALE;
                        break;

                    case "Female":
                        _sex = Sex.FEMALE;
                        break;

                    default:
                        _sex = Sex.MALE;
                        break;

                }
            }
        });



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

        _photoUri = fileUri;

    }

    public void openDatePicker(View view) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, dateListener, mYear, mMonth, mDay);
        dialog.show();

    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            int derp = 2;

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, arg1);
            c.set(Calendar.MONTH, arg2);
            c.set(Calendar.DAY_OF_MONTH, arg3);
            c.set(Calendar.HOUR_OF_DAY, 12);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            _birthday = c;

            setDateTextView((TextView) findViewById(R.id.new_athlete_birthday), c);
        }
    };

    public void setDateTextView(TextView view, Calendar c) {

        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        date.setCalendar(c);
        String output = "";

        view.setText(date.format(c.getTime()));

    }

    public void clickNext(View view) {

        if (checkInput()) {

            ImageView imgView = (ImageView) findViewById(R.id.new_athlete_picture);
            EditText name = (EditText) findViewById(R.id.new_athlete_name);
//            EditText height = (EditText) findViewById(R.id.new_athlete_height);
//            Spinner throwingClass = (Spinner) findViewById(R.id.new_athlete_class);

            Athlete athlete;

            if(_photoUri != null){

                athlete = new Athlete(_name, _birthday, _sex, _photoUri);

            } else {

                athlete = new Athlete(_name, _birthday, _sex);

            }

            Storage storage = new Storage(this);
            try {

                Athletes athletes = storage.loadAthletes();
                athletes.addAthlete(athlete);
                storage.saveAthletes(athletes);
                finish();

            } catch (Exception ex) {



            }

        }

    }

    public boolean checkInput() {

        if(_name != null && !_name.isEmpty() && _birthday != null && _sex != null) {

            return true;

        } else {

            return false;

        }

    }

}

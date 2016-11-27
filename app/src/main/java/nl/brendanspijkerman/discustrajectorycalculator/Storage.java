package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by Brendan on 28-11-2016.
 */

public class Storage {

    private Context mContext;

    Storage(Context _context) {

        mContext = _context;

    }

    public boolean saveAthlete(Athlete athlete) {

        Gson gson = new Gson();

        FileOutputStream outStream;
        FileInputStream inStream;
        String extension = ".json";
        String data = gson.toJson(athlete);
        int b = 32;
        b += 15;

        try {

            File outFile = new File(mContext.getExternalFilesDir("athletes"), (athlete.uniqueName + extension ));
            outStream = new FileOutputStream(outFile);
            try {

                outStream.write(data.getBytes());
                outStream.close();

            } catch (Exception e) {

            }

        } catch (IOException e) {


        }

        return false;

    }
}

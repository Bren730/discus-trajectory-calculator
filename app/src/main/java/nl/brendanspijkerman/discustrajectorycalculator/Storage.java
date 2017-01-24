package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.UUID;

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

    public boolean saveAthletes(Athletes athletes) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();

        FileOutputStream outStream;
        FileInputStream inStream;
        String extension = ".json";
        String data = gson.toJson(athletes);

        try {

            File outFile = new File(mContext.getExternalFilesDir("athletes"), ("athletes" + extension ));
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

    public Athlete loadAthlete(UUID uuid) {

        Athletes athletes = loadAthletes();

        for (Athlete athlete : athletes.entries) {

            if (athlete.id.equals(uuid)) {
                return athlete;
            }
        }

        return null;

    }

    public Athletes loadAthletes() {

        String extension = ".json";
        File file = new File(mContext.getExternalFilesDir("athletes"), ("athletes" + extension ));

        try {

            FileInputStream fin = new FileInputStream(file);
            String ret = convertStreamToString(fin);
            //Make sure you close all streams.
            fin.close();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriDeserializer())
                    .create();

            Athletes athletes = gson.fromJson(ret, Athletes.class);
            int b = 1;
            return athletes;

        } catch (FileNotFoundException e) {

            return new Athletes();

        } catch (Exception e) {

            return null;

        }

    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

}

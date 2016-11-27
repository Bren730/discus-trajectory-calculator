package nl.brendanspijkerman.discustrajectorycalculator;

import android.content.Context;
import android.icu.util.Output;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URI;

/**
 * Created by Brendan on 27-11-2016.
 */

public class Athlete {

    // AtomicInteger used for unique id creation
    private static AtomicInteger nextId = new AtomicInteger();

    public String name;
    public int id;
    public String uniqueName;
    public float height;
    public String throwingClass;

    Athlete(String _name, float _height, String _throwingClass) {

        this.name = _name;
        this.height = _height;
        this.throwingClass = _throwingClass;
        this.id = nextId.incrementAndGet();

        this.uniqueName = this.name + "_" + Integer.toString(this.id);

    }

}

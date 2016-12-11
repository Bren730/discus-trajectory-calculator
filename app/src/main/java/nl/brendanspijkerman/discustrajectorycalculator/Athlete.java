package nl.brendanspijkerman.discustrajectorycalculator;

import android.net.Uri;

import java.util.Calendar;
import java.util.UUID;

import nl.brendanspijkerman.discustrajectorycalculator.model.Sex;

/**
 * Created by Brendan on 27-11-2016.
 */

public class Athlete {

    public String name;
    public UUID id;

    public Calendar birthday;

    public Sex sex;

    public Uri photoUri;

    public String uniqueName;
    public String throwingClass;

    Athlete(String _name) {

        this.name = _name;
        this.id = UUID.randomUUID();

        this.uniqueName = this.name + "_" + this.id.toString();

    }

    Athlete(String _name, Calendar _birthday) {

        this.name = _name;
        this.id = UUID.randomUUID();

        this.birthday = _birthday;

        this.uniqueName = this.name + "_" + this.id.toString();

    }

    Athlete(String _name, Calendar _birthday, Sex _sex) {

        this.name = _name;
        this.id = UUID.randomUUID();

        this.birthday = _birthday;

        this.sex = _sex;

        this.uniqueName = this.name + "_" + this.id.toString();

    }

    Athlete(String _name, Calendar _birthday, Sex _sex, Uri _photoUri) {

        this.name = _name;
        this.id = UUID.randomUUID();

        this.birthday = _birthday;

        this.sex = _sex;

        this.photoUri = _photoUri;

        this.uniqueName = this.name + "_" + this.id.toString();

    }

}
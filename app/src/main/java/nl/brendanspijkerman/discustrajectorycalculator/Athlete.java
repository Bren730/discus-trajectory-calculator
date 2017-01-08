package nl.brendanspijkerman.discustrajectorycalculator;

import android.net.Uri;

import java.util.Calendar;
import java.util.UUID;

import nl.brendanspijkerman.discustrajectorycalculator.model.Gender;

/**
 * Created by Brendan on 27-11-2016.
 */

public class Athlete {

    public String name;
    public UUID id;

    public Calendar birthday;

    public Gender gender;

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

    Athlete(String _name, Calendar _birthday, Gender _gender) {

        this.name = _name;
        this.id = UUID.randomUUID();

        this.birthday = _birthday;

        this.gender = _gender;

        this.uniqueName = this.name + "_" + this.id.toString();

    }

    Athlete(String _name, Calendar _birthday, Gender _gender, Uri _photoUri) {

        this.name = _name;
        this.id = UUID.randomUUID();

        this.birthday = _birthday;

        this.gender = _gender;

        this.photoUri = _photoUri;

        this.uniqueName = this.name + "_" + this.id.toString();

    }

    public String getName() {

        return name;

    }

    public void setName(String _name) {

        this.name = _name;

    }

}
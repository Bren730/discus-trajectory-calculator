package nl.brendanspijkerman.discustrajectorycalculator;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by brendan on 11/12/2016.
 */

public class Athletes {

    public UUID id;

    ArrayList<Athlete> entries = new ArrayList<Athlete>();

    Athletes() {

        this.id = UUID.randomUUID();

    }

    boolean addAthlete(Athlete athlete) {

        if (!this.entries.contains(athlete)) {

            this.entries.add(athlete);
            return true;

        } else {

            return false;

        }

    }

}
package nl.brendanspijkerman.discustrajectorycalculator;

import java.util.UUID;

/**
 * Created by brendan on 11/12/2016.
 */

public class Coach {

    public String name;
    public UUID id;
    public String uniqueName;

    Coach(String _name) {

        this.name = _name;
        this.id = UUID.randomUUID();

        this.uniqueName = this.name + "_" + this.id.toString();

    }

}

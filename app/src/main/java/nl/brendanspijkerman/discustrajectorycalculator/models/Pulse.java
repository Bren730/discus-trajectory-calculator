package nl.brendanspijkerman.discustrajectorycalculator.models;

/**
 * Created by Brendan on 8-1-2017.
 */

public class Pulse {

    byte skip;
    byte rotor;
    byte data;

    Pulse(byte _skip, byte _rotor, byte _data){

        skip = _skip;
        rotor = _rotor;
        data = _data;

    }
}

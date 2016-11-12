package nl.brendanspijkerman.discustrajectorycalculator;

import java.io.Serializable;

/**
 * Created by Brendan on 12-11-2016.
 */

public class Variables implements Serializable {

    // Average gravity (m/s^2)
    double g = 9.81;
    // Average air density (kg/m^3)
    double rho = 1.225;
    // Average release speed (m/s)
    double v0;
    // Standard release angle (deg)
    double thetaRelease0;
    double thetaMotion0;
    // Standard attack angle (deg)
    double thetaAttack0;
    double thetaInclination0;

    // Standard stalling angle (deg)
    double thetaStall = 30;
    double vx0;
    double vy0;
    // Standard mass (kg)
    double m = 2;
    // Standard discus diameter
    double discusD = 0.22;
    // Standard discus height
    double discusH = 0.045;
    // Standard minimum drag coefficient (dimensionless)
    double cDMin = 0.04;
    // Standard maximum drag coefficient (dimensionless)
    double cDMax = 1.1;
    // Average release height (m)
    double y0;
    // Default x starting distance
    double x0 = 0;
    // Standard delta time interval (s)
    double deltaT = 0.01;
    // Default wind speed
    double vWind = 0;
    // Default max simulation time (s)
    double tMax = 30;

    // Set the raw performance values as static
    Variables(double _v0, double _theta0, double _thetaAttack0, double _y0) {


        v0 = _v0;
        thetaRelease0 = _theta0;
        thetaAttack0 = _thetaAttack0;
        y0 = _y0;

        thetaMotion0 = thetaRelease0;
        thetaInclination0 = thetaRelease0 + thetaAttack0;
        vx0 = v0 * Math.cos(rad(thetaRelease0));
        vy0 = v0 * Math.sin(rad(thetaRelease0));

    }

    // Class to hold all default values
    public static class defVal {

        // Average gravity (m/s^2)
        static double g = 9.81;
        // Average air density (kg/m^3)
        static double rho = 1.225;
        // Average release speed (m/s)
        static double v0 = 23.6;
        // Standard release angle (deg)
        static double thetaRelease0 = 35;
        static double thetaMotion0 = 35;
        // Standard attack angle (deg)
        static double thetaAttack0 = 0;
        static double thetaInclination0 = thetaRelease0 + thetaAttack0;

        // Standard stalling angle (deg)
        static double thetaStall = 30;
        static double vx0 = v0 * Math.cos(rad(thetaRelease0));
        static double vy0 = v0 * Math.sin(rad(thetaRelease0));
        // Standard mass (kg)
        static double m = 2;
        // Standard discus diameter
        static double discusD = 0.22;
        // Standard discus height
        static double discusH = 0.045;
        // Standard minimum drag coefficient (dimensionless)
        static double cDMin = 0.04;
        // Standard maximum drag coefficient (dimensionless)
        static double cDMax = 1.1;
        // Average release height (m)
        static double y0 = 1.492;
        // Default x starting distance
        static double x0 = 0;
        // Standard delta time interval (s)
        static double deltaT = 0.01;
        // Default wind speed
        static double vWind = 0;
        // Default max simulation time (s)
        static double tMax = 30;

    }

    static double rad(double deg) {

        double rad = (deg * Math.PI) / 180;
        return rad;

    }

    static double deg(double rad) {

        double deg = (rad * 180) / Math.PI;
        return deg;

    }

}

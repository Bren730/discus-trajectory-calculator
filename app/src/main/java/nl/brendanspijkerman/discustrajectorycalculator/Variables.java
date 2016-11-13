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
        static double thetaMotion0 = thetaRelease0;
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

    public static class max{

        // Average gravity (m/s^2)
        static double g = 9.83;
        // Average air density (kg/m^3)
        static double rho = 1.3;
        // Average release speed (m/s)
        static double v0 = 30;
        // Standard release angle (deg)
        static double thetaRelease0 = 89;
        static double thetaMotion0 = thetaRelease0;
        // Standard attack angle (deg)
        static double thetaAttack0 = 90;
        // Standard mass (kg)
        static double m = 2.5;
        // Standard discus diameter
        static double discusD = 0.221;
        // Standard discus height
        static double discusH = 0.046;
        // Average release height (m)
        static double y0 = 2;
        // Standard delta time interval (s)
        static double deltaT = 0.05;
        // Default wind speed
        static double vWind = 20;

    }

    public static class min{

        // Average gravity (m/s^2)
        static double g = 9.76;
        // Average air density (kg/m^3)
        static double rho = 1.15;
        // Average release speed (m/s)
        static double v0 = 15;
        // Standard release angle (deg)
        static double thetaRelease0 = 0;
        static double thetaMotion0 = thetaRelease0;
        // Standard attack angle (deg)
        static double thetaAttack0 = -90;
        // Standard mass (kg)
        static double m = 1;
        // Standard discus diameter
        static double discusD = 0.180;
        // Standard discus height
        static double discusH = 0.037;
        // Average release height (m)
        static double y0 = 1;
        // Standard delta time interval (s)
        static double deltaT = 0.005;
        // Default wind speed
        static double vWind = -20;

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

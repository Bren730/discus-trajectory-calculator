package nl.brendanspijkerman.discustrajectorycalculator;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Brendan on 12-11-2016.
 */

public class AirResistanceModel {

    // The version of the model
    public static int[] version = {0, 1, 0};

    Variables variables;

    AirResistanceModel(Variables _variables) {

        variables = _variables;

    }

    public Trajectory calculateTrajectory() {

        double v = variables.v0;
        double x = variables.x0;
        double y = variables.y0;
        double thetaRelease = variables.thetaRelease0;
        // Make sure that the x and y components are recalculated each time
        variables.vx0 = v * Math.cos(rad(thetaRelease));
        variables.vy0 = v * Math.sin(rad(thetaRelease));
        double vx = variables.vx0;
        double vy = variables.vy0;
        double vWind = variables.vWind;
        double vRelX = vx - vWind;
        double vRel = Math.sqrt(Math.pow(vRelX, 2) + Math.pow(vy, 2));
        double g = variables.g;
        double rho = variables.rho;
        double m = variables.m;
        double thetaAttack = variables.thetaAttack0;
        // Make sure that the angles at t0 are recalculated each time
        variables.thetaMotion0 = thetaRelease;
        variables.thetaInclination0 = thetaRelease + thetaAttack;
        double thetaMotion = variables.thetaMotion0;
        double thetaInclination = variables.thetaInclination0;
        double thetaMotionRel = deg(Math.atan(vy / vRelX));
        double thetaAttackRel = thetaInclination - thetaMotionRel;
        double cD = cDrag(thetaAttackRel, Variables.defVal.cDMin, Variables.defVal.cDMax);
        double cL = cLift(thetaAttackRel);
        double discusD = variables.discusD;
        double discusH = variables.discusH;
        double liftToDragCoefficient = cL / cD;
        double A = surfaceArea(thetaAttackRel, discusD, discusH);
        double Fx = fAeroX(rho, vRel, cD, cL, A, thetaMotionRel);
        double ax = aAeroX(rho, vRel, cD, cL, A, thetaMotionRel, m);
        double Fy = fAeroY(rho, vRel, cD, cL, A, thetaMotionRel);
        double ay = -g + aAeroY(rho, vRel, cD, cL, A, thetaMotionRel, m);
        double t = 0;
        double deltaT = variables.deltaT;

        // Variables to store the newly generated data
        // ArrayList is used because objects can be added dynamically
        ArrayList<Telemetry> data = new ArrayList<Telemetry>();
        double xMax = 0;
        double yMax = 0;
        double pathLength = 0;

        while (y > 0 && t < variables.tMax) {

            // Set all telemetry values
            Telemetry telemetry = new Telemetry();
            telemetry.t = t;
            telemetry.x = x;
            telemetry.y = y;
            telemetry.v = v;
            telemetry.vRel= vRel;
            telemetry.vx = vx;
            telemetry.vRelX= vRelX;
            telemetry.vy = vy;
            telemetry.ax = ax;
            telemetry.ay = ay;
            telemetry.fAeroX = Fx;
            telemetry.fAeroY = Fy;
            telemetry.aAeroX = ax;
            telemetry.aAeroY = ay;
            telemetry.thetaMotion = thetaMotion;
            telemetry.thetaAttack = thetaAttack;
            telemetry.thetaMotionRel = thetaMotionRel;
            telemetry.thetaAttackRel = thetaAttackRel;
            telemetry.cD = cD;
            telemetry.cL = cL;
            telemetry.A = A;
            telemetry.liftToDragCoefficient = liftToDragCoefficient;

            t += deltaT;

            double prevX = x;
            double prevY = y;

            // calculate x and y coordinates based on the previously determined speed
            x += vx * deltaT;
            y += vy * deltaT;

            // Calculate the path length between the previous and current x y location
            pathLength += Math.sqrt(Math.pow(x - prevX, 2) + Math.pow(y - prevY, 2) );

            thetaMotion = deg(Math.atan(vy / vx));

            thetaAttack = thetaInclination - thetaMotion;

            v = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));

            vRelX = vx - vWind;
            vRel = Math.sqrt(Math.pow(vRelX, 2) + Math.pow(vy, 2));

            // The relative velocity also changes the relative angle of motion and thus the surface area exposed to air drag
            thetaMotionRel = deg(Math.atan(vy / vRelX));

            thetaAttackRel = thetaInclination - thetaMotionRel;

            cD = cDrag(thetaAttackRel, Variables.defVal.cDMin, Variables.defVal.cDMax);
            cL = cLift(thetaAttackRel);
            liftToDragCoefficient = cL / cD;
            A = surfaceArea(thetaAttackRel, discusD, discusH);

            // console.log(cL)

            Fx = fAeroX(rho, vRel, cD, cL, A, thetaMotionRel);
            Fy = fAeroY(rho, vRel, cD, cL, A, thetaMotionRel);

            // console.log(rho, vRel, cD, cL, a, thetaMotionRel)

            ax = aAeroX(rho, vRel, cD, cL, A, thetaMotionRel, m);
            ay = aAeroY(rho, vRel, cD, cL, A, thetaMotionRel, m);
            ay = -g + ay;

            vx += ax * deltaT;
            vy += ay * deltaT;

            data.add(telemetry);

            if (data.size() > 1 && x > xMax) {

                xMax = x;

            }

            if (data.size() > 1 && y > yMax) {

                yMax = y;

            }

        }

        Trajectory trajectory = new Trajectory(data, xMax, yMax, x, t, variables, "AirResistanceModel", version);

        return  trajectory;

    }

    static double cDrag(double thetaAttack, double cDMin, double cDMax) {

        if (thetaAttack > 90) {

            // A positive angle of attack greater than 90 is the equivalent of a negative angle of attack
            thetaAttack -= 180;

        }

        if (thetaAttack <= -90) {

            // A negative angle of attack smaller than -90 is the equivalent of a positive angle of attack
            thetaAttack += 180;

        }

        double cD = (thetaAttack / 90) * (cDMax - cDMin) + cDMin;

        // console.log(cD)

        return Math.abs(cD);

    }

    static double cLift(double thetaAttack) {

        // var cL = 2 * Math.PI * rad(thetaAttack)

        double direction = thetaAttack > 0? 1 : -1;

        if (thetaAttack > 90) {

            // A positive angle of attack greater than 90 is the equivalent of a negative angle of attack
            thetaAttack -= 180;
            direction = -1;

        }

        if (thetaAttack <= -90) {

            // A negative angle of attack smaller than -90 is the equivalent of a positive angle of attack
            thetaAttack += 180;
            direction = 1;

        }

        if (thetaAttack == 0) {

            direction = 0;

        }

        if (Math.abs(thetaAttack) == 90) {

            direction = 0;

        }

        thetaAttack = Math.abs(thetaAttack);

        double cL;

        if (thetaAttack < Variables.defVal.thetaStall) {

            cL = (thetaAttack / 30) * 0.9;
            // cL = 2 * Math.PI * rad(thetaAttack)

        } else if (thetaAttack < 35) {

            cL = 0.9 - (((thetaAttack - 30) / 5) * 0.3);

        } else if (thetaAttack < 70) {

            cL = 0.6 - (((thetaAttack - 35) / 35) * 0.2);

        } else {

            cL = 0.4 - (((thetaAttack - 70) / 20) * 0.4);

        }

        // console.log(cL)

        return cL * direction;

    }

    static double surfaceArea(double thetaAttack, double diameter, double height) {

        if (thetaAttack > 90) {

            // A positive angle of attack greater than 90 is the equivalent of a negative angle of attack
            thetaAttack -= 180;

        }

        if (thetaAttack <= -90) {

            // A negative angle of attack smaller than -90 is the equivalent of a positive angle of attack
            thetaAttack += 180;

        }

        // Max surface area is simply the surface area of a circle with radius 0.5 * diameter
        double aMax = Math.PI * Math.pow((diameter / 2), 2);

        // Polynomial components (obtained through Excel)
        double polynomial5 = -0.000000000734490808986996000000 * Math.pow(thetaAttack, 5);
        double polynomial4 = 0.000000196292760925362000000000 * Math.pow(thetaAttack, 4);
        double polynomial3 = -0.000020085728567986700000000000 * Math.pow(thetaAttack, 3);
        double polynomial2 = 0.000865257696037958000000000000 * Math.pow(thetaAttack, 2);
        double polynomial1 = -0.001000632147082570000000000000 * thetaAttack;
        double polynomial0 = 0.181257282109542000000000000000;

        // Calculate Area Coefficient with polynomial function
        double Ca = polynomial5 + polynomial4 + polynomial3 + polynomial2 + polynomial1 + polynomial0;

        // Calculate area based on max surface area
        double a = aMax * Ca;

        // Surface area cannot be negative. Return the absolute value
        return Math.abs(a);

    }

    static double fAeroX(double rho, double v, double cD, double cL, double a, double thetaMotion) {

        // Since lift is perpendicular to the angle of motion, we add 90° to thetaMotion

        // thetaMotion = Math.abs(thetaMotion)

        // var F = 0.5 * rho * Math.pow(v, 2) * (Math.cos(rad(thetaMotion)) * cD + Math.sin(rad(thetaMotion)) * cL) * a
        double F = 0.5 * rho * Math.pow(v, 2) * (-Math.cos(rad(thetaMotion)) * cD - Math.cos(rad(90 - thetaMotion)) * cL) * a;

        return F;

    }

    static double fAeroY(double rho, double v, double cD, double cL, double a, double thetaMotion) {

        // Since lift is perpendicular to the angle of motion, we add 90° to thetaMotion

        // thetaMotion = Math.abs(thetaMotion)

        // var F = 0.5 * rho * Math.pow(v, 2) * (Math.cos(rad(thetaMotion)) * cL - Math.sin(rad(thetaMotion)) * cD) * a
        double F = 0.5 * rho * Math.pow(v, 2) * (Math.sin(rad(90 - thetaMotion)) * cL - Math.sin(rad(thetaMotion)) * cD) * a;

        return F;

    }

    static double aAeroX(double rho, double v, double cD, double cL, double a, double thetaMotion, double m) {

        // console.log(rho, v, cD, cL, a, thetaMotion, m)

        return fAeroX(rho, v, cD, cL, a, thetaMotion) / m;

    }

    static double aAeroY(double rho, double v, double cD, double cL, double a, double thetaMotion, double m) {

        return fAeroY(rho, v, cD, cL, a, thetaMotion) / m;

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
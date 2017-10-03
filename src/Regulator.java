import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

/**
 * Created by Macken on 2017-09-25.
 */
class Regulator {
    //Speed given by regulator calculations
    private double v1;

    //Desired speed
    private double vDes;

    //Desired distance
    private double dDes;

    //Acceleration
    private double accFactor;

    //??
    //private double a0;

    //Multiplier
    private double k;

    //Integrating factor
    private double i;

    //Acceleration
    private double i_acc;

    //dist1 and dist2 is used for simulator (I think), deltaDist is delta of dist1 and dist 2
    //private double dist1;
    //private double dist2;
    //private double deltaDist;

    //Derivating factor
    private double d;

    //Last error
    private double lastEr;

    //Ticks
    //private int s;

    public Regulator() {

        v1 = 0;
        dDes = 0.40;
        //a0 = 0.40;
        k = 236;
        //s = 0;
        i = 0.05;
        i_acc = 0;
        d = 0.2;
        lastEr = 0;

        /*deltaDist = 0.40;
        dist1 = a0;
        dist2 = 0;*/

    }

    /**
     * Made a public help method to ease the transition from Simulator to MOPED version.
     * Change calcNewSpeed() to public means we can use that method directly.
     * @param distance Sensor reading in meter.
     * @return New speed to work towards (regulated/desired speed).
     */
    public int initNewCalc(double distance){
        calcNewSpeed(distance);

        return (int)v1;
    }

    /**
     * Used to calculate distance travelled for both Cars. The differential between the distances
     * is the distance between the cars, which is used in the simulator to mimick the result given from the Sensor
     * on the MOPED.
     * NOT NEEDED for the regulator used by MOPED, we get the result from the CAN bus on the car using
     * UltraSonicSensor class.
     * @param speed2 Speed of the car.
     * @return  Distance between the cars.
     */
    /*private double readSensor(double speed2) {
        dist1 += (speed2 / (3.6 * 40));
        dist2 += (v1 / (3.6 * 40));

        return dist1 - dist2;
    }*/

    /**
     * The simulator version, some changes needed to use in with the MOPED data.
     *
     * Parameter used in Simulator is speed2 = speed of the car in front.
     *
     * @param sensorValue Sensor reading in meter.
     */
    private void calcNewSpeed(double sensorValue) {
        //deltaDist = readSensor(speed2);

        double error = sensorValue -  dDes;
        i_acc += error * i;

        vDes = error * k; //+ i_acc + (-error +lastEr) * d;
        v1 += accFactor * (vDes - v1);

        lastEr = error;
    }
}

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

/**
 * Created by Macken on 2017-09-25.
 */
class Regulator {
    /* Speed given by regulator calculations */
    private double v1;

    /* Last speed given to MOPED */
    private double lastSpeed;

    /* Desired speed */
    private double vDes;

    /* Desired distance */
    private double dDes;

    /* Acceleration */
    private double accFactor;

    /* ?? */
    /* private double a0; */

    /* Multiplier */
    private double k;

    /* Integrating factor */
    private double i;

    /* Acceleration */
    private double i_acc;

    /* dist1 and dist2 is used for simulator (I think), deltaDist is delta of dist1 and dist 2 */
    /* private double dist1; */
    /* private double dist2; */
    /* private double deltaDist; */

    /* Derivating factor */
    private double d;

    /* Last error */
    private double lastEr;

    /* K = 0.48 */
    /* T0 = 9.3 */
    /* I = 4.65 */
    /* D = 1.16 */

    /* Ticks */
    /* private int s; */

    public Regulator() {

        v1 = 0;
        dDes = 20;
        /* a0 = 0.40; */
        k = 0.2;
        /* s = 0; */
        i = 0.01;
        i_acc = 0;
        d = 0.4;
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
    public void initNewCalc(double distance){
        System.out.println("Distance: " + distance);
        calcNewSpeed(distance);
        System.out.println("New Speed: " + v1);
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
        double error = sensorValue - dDes;
            i_acc += error * i;

            i_acc = i_acc > 2 ? 2 : i_acc;
            i_acc = i_acc < -4 ? -4 : i_acc;

            v1 = error * k + i_acc + (error - lastEr) * d;

            if (v1 > 0)
                v1 += 8;

            if (v1 < 11)
                v1 = 0;

            lastEr = error;
    }

    public int getSpeed() {
        return (int)v1;
    }
}

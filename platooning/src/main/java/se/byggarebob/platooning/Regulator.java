package se.byggarebob.platooning;

/**
 * This class regulates the speed based on the distance to an object in front of the car.
 *
 * @author Arvid Wiklund
 * @author Hugo Frost
 * @author Kalle Ängermark
 */
class Regulator {

  /** Current speed given to MOPED */
  private double v1;

  /** Desired distance */
  private double dDes;

  /** Amplification factor */
  private double k;

  /** Integral factor */
  private double i;

  /** Accumulated integral factor*/
  private double i_acc;

  /** Deriving factor */
  private double d;

  /** Last error */
  private double lastEr;

  /** Limitations to regulator */
  private double maxSpeed;
  private double minSpeed;

  /**
   * Instantiates all variables.
   * Easy to change the different parameters, for example max speed or desired distance
   * to object in front.
   */
  public Regulator() {

    v1 = 0;
    dDes = 20;
    k = 0.3;
    i = 0.04;
    i_acc = 0;
    d = 0.8;
    lastEr = 0;

    maxSpeed = 80;
    minSpeed = -80;
  }

  /**
   * Made a public help method to ease the transition from Simulator to MOPED
   * version. * Change calcNewSpeed() to public means we can use that method
   * directly.
   *
   * @param distance Sensor reading in meter.
   */
  public void initNewCalc(double distance) {
    calcNewSpeed(distance);
  }

  /**
   * The calculations to get new speed for car.
   * Uses predetermined K, I and D factors implementing a PID regulator.
   * Implemented limitations to keep calculations realistic.
   *
   * @param sensorValue Sensor reading in cm.
   */
  private void calcNewSpeed(double sensorValue) {
    double error = sensorValue - dDes;

    /**The sensor values are not always accurate and we don't want to over regulate on accumulated
    error*/
    if (Math.abs(error) < 100) {
      i_acc += error * i;
    }

    /** Accumulated error started affecting the speed to much so it was limited to -4 < i_acc < 10*/
    i_acc = i_acc > 10 ? 10 : i_acc;
    i_acc = i_acc < -4 ? -4 : i_acc;

    /** The new speed is equal to the sum of all three calculations.*/
    v1 = error * k + i_acc + (error - lastEr) * d;

    /** Check that the new calculated speed is within given limitations*/
    if (v1 < minSpeed) {
      v1 = minSpeed;
    } else if (v1 > maxSpeed) {
      v1 = maxSpeed;
    }

    /** Save the previous error*/
    lastEr = error;
  }

  /**
   * Simple return method.
   * Casts v1 to an integer
   *
   * @return int Current speed.
   */
  public int getSpeed() {
    return (int) v1;
  }
}

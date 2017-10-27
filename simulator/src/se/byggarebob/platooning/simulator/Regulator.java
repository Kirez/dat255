package se.byggarebob.platooning.simulator;

/**
 * A general control system used in the simulator.
 *
 * @author Karl Ängermark
 */
class Regulator {

  /** The velocity of the car. */
  private double v1;

  /** The amplification of the control system. */
  private double k;

  /** The integrating factor of the control system. */
  private double i;

  /** The accumulated integration. */
  private double i_acc;

  /** The derivative factor of the control system. */
  private double d;

  /** The previous received error. */
  private double lastEr;

  /**
   * Instantiates a new regulator.
   */
  Regulator() {
    v1 = 0; /* k = 236; */
    k = 0.32; /* k = 0.5; */
    //s = 0;
    i = 0.000;
    i_acc = 0;
    d = 0.00;
    lastEr = 0;
  }

  /**
   * Gets the new speed.
   *
   * @param accFactor the acc factor
   * @param distance the distance
   * @return the new speed
   */
  public double getNewSpeed(double accFactor, double distance) {
    double error = -40 + distance;
    i_acc += error * i; /* vDes = error * k; */
    double vDes = error * k + i_acc + (-error + lastEr) * d;
    v1 += 0.1 * (vDes - v1);
    lastEr = error;
    return v1;
  }
}
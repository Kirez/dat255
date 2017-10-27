package se.byggarebob.platooning;

/**
 * A handler for multiple classes working with the distance control.
 *
 * @author Arvid Wiklund
 * @author Hugo Frost
 */
public class ACC implements Runnable {

  /** Tells the program if it should terminate or not. */
  private boolean stopFlagged = false;

  /** Instance of the class MotorControl. */
  private MotorControl mc;

  /** Instance of the class UltraSonicSensor. */
  private UltraSonicSensor sensor;

  /** Instance of the class Regulator, for ACC. */
  private Regulator regulator;

  /** Is the sensor value from the MOPED, given by the class UltraSonicSensor. */
  private double distance;

  /**
   * Instantiates a new acc.
   *
   * @param mc ACC have its own MotorControl to control the motor value in the
   * moped
   * @param sensor ACC have its own UltraSonicSensor to read the sensor values
   * from the MOPED.
   */
  public ACC(MotorControl mc, UltraSonicSensor sensor) {
    this.sensor = sensor;
    this.mc = mc;
    distance = 0;
    regulator = new Regulator();
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    while (!stopFlagged) {
      try {
        distance = sensor.getDistance();
        while (distance == -1) {
          distance = sensor.getDistance();
          Thread.sleep(10);
        }
        regulator.initNewCalc(distance);
        mc.setSpeed(regulator.getSpeed());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Is called when the program should stop.
   * <p>
   * Sets the motor value to 0 to make sure the MOPED is not running and sets
   * the boolean to true to exit the loop in run().
   */
  public void stop() {
    mc.setSpeed(0);
    stopFlagged = true;
  }
}
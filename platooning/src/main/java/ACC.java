// TODO: Auto-generated Javadoc
/**
 * The Class ACC.
 */
public class ACC implements Runnable {

  /** The stop flagged. */
  private boolean stopFlagged = false;
  
  /** The mc. */
  private MotorControl mc;
  
  /** The sensor. */
  private UltraSonicSensor sensor;
  
  /** The regulator. */
  private Regulator regulator;
  
  /** The distance. */
  private double distance;

  /**
   * Instantiates a new acc.
   *
   * @param mc the mc
   * @param sensor the sensor
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
   * Stop.
   */
  public void stop() {
    mc.setSpeed(0);
    stopFlagged = true;
  }
}
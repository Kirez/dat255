import java.util.List;

/**
 * This interface is supposed create basic methods to get data from the MOPED using the CarAPI.
 * @author Timmy Truong, Arvid Wiklund, Hamza Kadric
 */
public interface KeepDistance {

  /**
   * This method reads from sensor and returns data in form of a double
   * containing the distance to the object in front in metres.
   *
   * The return value shall be in metres as we do not want variables to have
   * different units to complicate the calculations.
   *
   * @return a double containing the value of the distance to the object in front read from the sensor
   */
  public double readSensor();

  /**
   * Set speed
   * Write a double to the MOPEDs engine in order to change the speed.
   *
   * @param speed corresponding to the new speed
   */
  public void setSpeed(double speed);

  /**
   * Read speed
   * Read data from the odometer to get the current speed of the MOPED.
   *
   * @return Speed of the car in m/s
   */
  public double readSpeed();
}

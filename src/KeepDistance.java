/**
 * This interface is supposed create basic methods to get data from the MOPED using the CarAPI.
 * @author Timmy Truong, Arvid Wiklund, Hamza Kadric
 */
public interface KeepDistance {

  /**
   * Reads data from the sensor to get current distance to object in front.
   *
   * @return Double value of the distance read from sensor in mm.
   */
  double readSensor();

  /**
   * Writes a double to the MOPEDs engine in order to change the speed.
   *
   * @param speed corresponding to the new speed in m/s.
   */
  void setSpeed(double speed);

  /**
   * Reads data from the odometer to get the current speed of the MOPED.
   * 
   * @return Speed of the car in m/s
   */
  double getSpeed();
}

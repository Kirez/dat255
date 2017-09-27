/**
 * This interface is supposed create basic methods to get data from the MOPED using the CarAPI.
 * @author Hamza Kadric
 * @author Timmy Truong
 * @author Arvid Wiklund
 */
public interface Moveable {

  /**
   * Set speed
   * Write a double to the MOPEDs engine in order to change the speed.
   *
   * @param speed corresponding to the new speed
   */
  void setSpeed(byte speed);

  /**
   * Read speed
   * Read data from the odometer to get the current speed of the MOPED.
   *
   * @return speed of the car in cm/s
   */
  double getSpeed();
}

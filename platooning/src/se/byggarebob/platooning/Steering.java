package se.byggarebob.platooning;

// TODO: Auto-generated Javadoc
/**
 * This interface is supposed create basic methods to get data from the MOPED
 * using the CarAPI.
 *
 * @author Hugo Frost
 */
public interface Steering {

  /**
   * Write an int to the MOPEDs servo in order to steer.
   *
   * @param steering corresponding to the new speed
   */
  void steer(int steering);
}
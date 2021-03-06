package se.byggarebob.platooning;

/**
 * Handles interfacing between the software layer and "bare metal".
 *
 * @author Hugo Frost
 */
public class ServoControl implements Steering {


  /** The shared instance of CAN. */
  private CAN can;

  /**
   * Instantiates a new servo control.
   *
   * @param can the shared instance of CAN from which values should be read
   */
  public ServoControl(CAN can) {
    this.can = can;
  }

  /* (non-Javadoc)
   * @see Steering#steer(int)
   */
  public void steer(int steering) {
    if (steering > 100) {
      steering = 100;
    } else if (steering < -100) {
      steering = -100;
    }
    try {
      can.sendSteerValue((byte) steering);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

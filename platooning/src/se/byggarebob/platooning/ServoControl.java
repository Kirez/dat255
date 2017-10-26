package se.byggarebob.platooning;

// TODO: Auto-generated Javadoc
/**
 * Created by hugfro on 2017-10-03.
 */
public class ServoControl implements Steering {

  /** The can. */
  private CAN can;

  /**
   * Instantiates a new servo control.
   *
   * @param can the can
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
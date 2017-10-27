package se.byggarebob.platooning;

// TODO: Auto-generated Javadoc
/**
 * The Class MotorControl.
 *
 * @author Hugo Frost
 * @author Arvid Wiklund
 * @author Erik Källberg
 */
public class MotorControl implements IMovable {

  /** The last speed sent to the can */
  private int lastSpeed = 0;

  /** The shared instance of CAN */
  private CAN can;

  /**
   * Instantiates a new motor control. And sets a constant steering value
   * to negate eventual error in steering caused by hardware
   *
   * @param can the shared instance of CAN, used to send can-frames
   */
  public MotorControl(CAN can) {
    this.can = can;
    try {
      byte steerValue = -8;
      can.sendMotorAndSteerValue((byte) 0, steerValue);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /* (non-Javadoc)
   * @see IMovable#getSpeed()
   */
  public double getSpeed() {
    return lastSpeed;
  }

  /* (non-Javadoc)
   * @see IMovable#setSpeed(int)
   */
  public void setSpeed(int speed) {
    if (speed > 127) {
      speed = 127;
    } else if (speed < -127) {
      speed = -127;
    }

		/* send CAN */
    try {
      if (speed == 0) {
        speed = -1;
      }

      if (lastSpeed < 0 && speed > 0) {
        can.sendMotorValue((byte) 0);
      }
      can.sendMotorValue((byte) speed);
      lastSpeed = speed;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

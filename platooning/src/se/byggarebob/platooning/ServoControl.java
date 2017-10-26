package se.byggarebob.platooning;

/**
 * Created by hugfro on 2017-10-03.
 */
public class ServoControl implements Steering {

  private CAN can;

  public ServoControl(CAN can) {
    this.can = can;
  }

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
public class MotorControl implements IMovable {

  private int lastSpeed = 0;
  private CAN can;

  public MotorControl(CAN can) {
    this.can = can;
    try {
      byte steerValue = -8;
      can.sendMotorAndSteerValue((byte) 0, steerValue);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public double getSpeed() {
    return lastSpeed;
  }

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
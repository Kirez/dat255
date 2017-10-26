package se.byggarebob.platooning;

public class ALCRegulator {

  private ServoControl servo;
  private int k;

  public ALCRegulator(ServoControl servo) {
    this.servo = servo;
    k = 1;
  }

  public void calcSteering(int offset) {
    int angle = k * offset;
    servo.steer(angle);
  }
}

package se.byggarebob.platooning;

/**
 * Regulator for lateral control.
 */
public class ALCRegulator {

  /** The servo inherited from the main thread. */
  private ServoControl servo;

  /** Amplification factor for the control system. */
  private int k;

  /**
   * Instantiates a new ALC regulator.
   *
   * @param servo the servo
   */
  public ALCRegulator(ServoControl servo) {
    this.servo = servo;
    k = 1;
  }

  /**
   * Calculate the desired steering using P regulation for the maximum possible
   * minimization of steering error regarding the ALC protocol.
   *
   * @param offset the offset
   */
  public void calcSteering(int offset) {
    int angle = k * offset;
    servo.steer(angle);
  }
}

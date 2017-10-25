// TODO: Auto-generated Javadoc
/**
 * The Class ALCRegulator.
 */
public class ALCRegulator {

  /** The servo. */
  private ServoControl servo;
  
  /** The k. */
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
   * Calc steering.
   *
   * @param offset the offset
   */
  public void calcSteering(int offset) {
    int angle = k * offset;
    servo.steer(angle);
  }
}

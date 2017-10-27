package se.byggarebob.platooning;

import java.io.IOException;

/**
 * The main thread for the ALC.
 *
 * @author Erik Källberg
 * @author Karl Ängermark
 */
public class ALC implements Runnable {

  /** The servo for steering. */
  private ServoControl servo;

  /**
   * Instantiates a new alc.
   *
   * @param servo the servo
   */
  public ALC(ServoControl servo) {
    this.servo = servo;
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try {
      new Thread(new ImageServer(new ALCRegulator(servo))).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
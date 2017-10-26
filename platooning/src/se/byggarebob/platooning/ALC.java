package se.byggarebob.platooning;

import java.io.IOException;

public class ALC implements Runnable {

  private ServoControl servo;

  public ALC(ServoControl servo) {
    this.servo = servo;
  }

  @Override
  public void run() {
    try {
      new Thread(new ImageServer(new ALCRegulator(servo))).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
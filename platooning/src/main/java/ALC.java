import java.io.IOException;
import java.io.InputStream;

public class ALC implements Runnable {

  private boolean stopFlagged;
  private Process cameraProcess;
  private InputStream cameraStream;
  private int k;
  private ServoControl servo;
  private int offsetError;

  public ALC(ServoControl servo) {
    k = 10;
    this.servo = servo;
    offsetError = 0;
  }

  @Override
  public void run() {
    try {
      new Thread(new ImageServer(new ALCRegulator(servo))).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    stopFlagged = true;
  }

  private void calcSteering(int offset) {
    System.out.println(offset);
    if (offset > 100) {
      offset = 100;
    } else if (offset < -100) {
      offset = -100;
    }
    int angle = k * offset;
    servo.steer(angle);
  }
}
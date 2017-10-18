import java.io.IOException;
import java.io.InputStream;

public class ALC implements Runnable {

  private boolean stopFlagged;
  /*private Process cameraProcess;
  private InputStream cameraStream;
  private int k;*/
  private ServoControl servo;
  //private int offsetError;

  public ALC(ServoControl servo) {
    //k = 1;
    this.servo = servo;
    //offsetError = 0;
  }

  @Override
  public void run() {
    if (!stopFlagged) {
      try {
        new Thread(new ImageServer(new ALCRegulator(servo))).start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void stop() {
    stopFlagged = true;
  }

  /*private void calcSteering(int offset) {
    int angle = k * offset;
    servo.steer(angle);
  }*/
}
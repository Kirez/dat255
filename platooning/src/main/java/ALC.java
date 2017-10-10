import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class ALC implements Runnable {

    private boolean stopFlagged;
    private Process cameraProcess;
    private InputStream cameraStream;
    private int k;
    private ServoControl servo;

    private int offsetError;

    public ALC(ServoControl servo) {

        k = 1;
        this.servo = servo;

        offsetError = 0;
    }

    @Override
    public void run() {

   /*      String argv = "raspivid -l -o  - --framerate 10 -w 1920 -h 432 -t 1000000 | nc 2222"; */
       String argv = "raspivid -l -o  tcp://0.0.0.0:2222 --framerate 10 -w 1920 -h 432 -t 1000000";
        try {
            cameraProcess = Runtime.getRuntime().exec(argv);
     /*        cameraStream = cameraProcess.getInputStream(); */
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        while (!stopFlagged) {

            try {

                /* send...... */


                /* receive offsetError..... */

                /* calcSteering(offsetError); */


                throw new InterruptedException("error");

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void stop() {
        stopFlagged = true;
    }

    private void calcSteering(int offset) {

        int angle = k * offset;

        servo.steer(angle);


    }
}


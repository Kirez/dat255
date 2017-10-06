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

    public ALC(CAN can) {

        k = 1;
        servo = new ServoControl(can);

        offsetError = 0;
    }

    @Override
    public void run() {

        String argv = "raspistill -o - --raw --timeout 1 --roi 0.00,0.4,1.00,0.2";

        try {
            cameraProcess = Runtime.getRuntime().exec(argv);
            cameraStream = cameraProcess.getInputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        while (!stopFlagged) {

            try {

                //send......


                //receive...

                //calcSteering(offsetError);
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


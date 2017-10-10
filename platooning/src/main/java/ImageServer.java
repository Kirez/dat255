import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import static org.opencv.core.Core.*;
import java.io.IOException;

/**
 * Created by Macken on 2017-10-06.
 */


public class ImageServer {

   private ImageProcessing p = new ImageProcessing();



    public static void main () throws IOException {


        String argv = "nc -l 2222";
        Process receive = Runtime.getRuntime().exec(argv);
        VideoCapture stream = new VideoCapture(argv);
        stream.open("(192.168.43.230?stream=mpeg"); /*  a mjpeg , ipcam stream */
        Mat frame = new Mat();

        while (true) {

            if (stream.read(frame)) {
            	


            }

        }


    }

    public void receive() {




    /*   double offset =  p.findCircles().getxOffset(); */

    /*   send(offset); */

    }

    public void send(double data) {

        /* SEND DATA */


    }


}

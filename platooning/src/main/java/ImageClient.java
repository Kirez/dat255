import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import static org.opencv.core.Core.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by Macken on 2017-10-06.
 */


public class ImageClient implements Runnable {
    @SuppressWarnings("unused")
    private double xOfset, xCenter,yCenter;
    private ImageProcessing imgPr;
    private ProcessedImage proImg;
    private VideoCapture stream;
    private byte[] sendData;
    private InetAddress IPAddress;
    private Socket clientSocket;



    public ImageClient() throws IOException {
        System.out.println("Connecting...");
        clientSocket = new Socket("localhost", 2223);
        System.out.println("Connected");
        imgPr = new ImageProcessing();
        stream = new VideoCapture();
        receive();
    }

    public void receive() {
        stream.open("tcp://192.168.43.230:2222");
        System.out.println("Started");

        Mat frame = new Mat();
        if (stream.isOpened()) {
            while (true) {
                if (stream.read(frame)) {
                    proImg = imgPr.getProcessedImage(frame);
                    if (proImg != null) {
                        xOfset = proImg.getxOffset();
                        xCenter = proImg.getCenterX();
                        yCenter = proImg.getCenterY();
                        System.out.println(xOfset);
                        send();
                    }

                }
            }
        }
    }

    public void send() {
        try {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(""+xOfset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        receive();

    }
}
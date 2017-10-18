import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * Created by Macken on 2017-10-06.
 */


class ImageClient implements Runnable {
    @SuppressWarnings("unused")
    private double xOffset/*, xCenter,yCenter*/;
    private ImageProcessing imgPr;
    private VideoCapture stream;
    //private byte[] sendData;
    //private InetAddress IPAddress;
    private Socket clientSocket;


    public static void main(String [] args) throws IOException {
        new Thread(new ImageClient()).start();

    }

    private ImageClient() throws IOException {
        System.out.println("Connecting...");
        clientSocket = new Socket("192.168.43.230", 2223);
        System.out.println("Connected");
        imgPr = new ImageProcessing();
        stream = new VideoCapture();
        receive();
    }

    private void receive() {
        stream.open("tcp://192.168.43.230:2222");
        System.out.println("Started");

        Mat frame = new Mat();
        if (stream.isOpened()) {
            while (true) {
                if (stream.read(frame)) {
                    ProcessedImage proImg = imgPr.getProcessedImage(frame);
                    if (proImg != null) {
                        xOffset = proImg.getxOffset();
                        //xCenter = proImg.getCenterX();
                        //yCenter = proImg.getCenterY();
                        System.out.println(xOffset);
                        send();
                    }

                }
            }
        }
    }

    private void send() {
        try {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(""+ xOffset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        receive();

    }
}
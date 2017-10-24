package imageClient;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import static org.opencv.core.Core.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Created by calrless on 2017-10-06.
 */

public class ImageClient implements Runnable {

  @SuppressWarnings("unused")
  private int xOfset, xCenter, yCenter;
  private ImageProcessing imgPr;
  private ProcessedImage proImg;
  private VideoCapture stream;
  private InetAddress IPAddress;
  private Socket clientSocket;
  private OutputStream ostream;
  private DataOutputStream dos;

  public static void main(String[] args) throws IOException {
    new Thread(new ImageClient()).start();
  }

  public ImageClient() throws IOException {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    imgPr = new ImageProcessing();
    stream = new VideoCapture();

    receive();
  }

  public void receive() throws IOException {

    System.out.println("joining stream");
    stream.open("tcp://192.168.43.230:2222");
    System.out.println("Started");
    Mat frame = new Mat();
    if (stream.isOpened()) {
      System.out.println("connected to stream");
      while (true) {
        if (stream.read(frame)) {
          proImg = imgPr.getProcessedImage(frame);
          if (proImg != null) {

            xOfset = (int) proImg.getxOffset();
            xCenter = (int) proImg.getCenterX();
            yCenter = (int) proImg.getCenterY();

            send();
          }
        }
      }
    }
  }

  public void send() {
    try {
      byte[] sendD = new byte[300];
      byte[] recD = new byte[300];
      InetAddress IP = InetAddress.getByName("192.168.43.230");

      DatagramSocket socket = new DatagramSocket();
      sendD = ByteBuffer.allocate(4).putInt(xOfset).array();

      int x = java.nio.ByteBuffer.wrap(sendD).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
      DatagramPacket sendP = new DatagramPacket(sendD, sendD.length, IP, 9876);
      socket.send(sendP);
      //System.out.println("SENT: "+ xOfset);

      DatagramPacket recP = new DatagramPacket(recD, recD.length);
      socket.receive(recP);
      String msg = new String(recP.getData());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    try {
      receive();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
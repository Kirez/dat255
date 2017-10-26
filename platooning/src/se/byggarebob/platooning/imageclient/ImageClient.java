package se.byggarebob.platooning.imageclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * This class receives a stream with images from the MOPED, processes the image and sends an x value back.
 * @author Johannes Edenholm & Karl Ängermark
 */
public class ImageClient implements Runnable {

  /** The x offset. */
  private int xOffset;

  /** The img pr. */
  private ImageProcessing imgPr;

  /** The stream. */
  private VideoCapture stream;

  /**
   * Instantiates a new image client.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ImageClient() throws IOException {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    imgPr = new ImageProcessing();
    stream = new VideoCapture();

    receive();
  }

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void main(String[] args) throws IOException {
    new Thread(new ImageClient()).start();
  }

  /**
   * Receives a datastream of images and processes
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void receive() throws IOException {
    stream.open("tcp://192.168.43.230:2222");
    Mat frame = new Mat();
    if (stream.isOpened()) {
      System.out.println("connected to stream");
      while (true) {
        if (stream.read(frame)) {
          ProcessedImage proImg = imgPr.getProcessedImage(frame);
          if (proImg != null) {
            xOffset = (int) proImg.getxOffset();
            send();
          }
        }
      }
    }
  }

  /**
   * Sends a UDP message with the xOfset
   */
  public void send() {
    try {
      byte[] sendD;
      byte[] recD = new byte[300];
      InetAddress IP = InetAddress.getByName("192.168.43.230");

      DatagramSocket socket = new DatagramSocket();
      sendD = ByteBuffer.allocate(4).putInt(xOffset).array();

      DatagramPacket sendP = new DatagramPacket(sendD, sendD.length, IP, 9876);
      socket.send(sendP);

      DatagramPacket recP = new DatagramPacket(recD, recD.length);
      socket.receive(recP);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try {
      receive();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
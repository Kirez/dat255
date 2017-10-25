package imageClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

// TODO: Auto-generated Javadoc
/**
 * Created by calrless on 2017-10-06.
 */
public class ImageClient implements Runnable {

  /** The x offset. */
  @SuppressWarnings("unused")
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
   * Receive.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void receive() throws IOException {

    System.out.println("joining stream");
    stream.open("tcp://192.168.43.230:2222");
    System.out.println("Started");
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
   * Send.
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

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
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
package se.byggarebob.platooning;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Responsible for starting the stream on the TCU and accepting one connection.
 * Also receives data from client and adjusts steering to the data.
 *
 * @author Johannes Edenholm
 * @author Karl Ängermark
 * @author Linus Berglund
 */
public class ImageServer implements Runnable {

  /** The ALC (lateral) regulator. */
  private ALCRegulator ALCreg;

  /** The camera process. */
  private Process cameraProcess;

  /**
   * Instantiates a new image server.
   *
   * @param ALCreg the lateral regulator.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ImageServer(ALCRegulator ALCreg) throws IOException {
    this.ALCreg = ALCreg;
    String argv = "raspivid -l -o tcp://0.0.0.0:2222 --framerate 10 -w 1270 -h 292 -t 0 --mode 5";
    cameraProcess = Runtime.getRuntime().exec(argv);

    if (cameraProcess.isAlive()) {
      System.out.println("raspivid process active");
    } else {
      System.out.println("raspivid process not active");
    }
  }

  /*
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try {
      DatagramSocket serverSocket = new DatagramSocket(9876);
      System.out.println("listening on port 9876");
      byte[] send;
      byte[] receive = new byte[300];

      while (true) {

        DatagramPacket receivePack = new DatagramPacket(receive,
            receive.length);
        serverSocket.receive(receivePack);
        byte[] message = receivePack.getData();

        int x = java.nio.ByteBuffer.wrap(message).getInt();
        System.out.println("Sending to ALC : " + x);

        ALCreg.calcSteering(x);

        InetAddress IP = receivePack.getAddress();
        int port = receivePack.getPort();

        send = "OK".getBytes(StandardCharsets.UTF_8);
        DatagramPacket sendPack = new DatagramPacket(send, send.length, IP,
            port);
        serverSocket.send(sendPack);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

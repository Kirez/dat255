import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Created by Macken on 2017-10-06.
 */
public class ImageServer implements Runnable {

  private ALCRegulator ALCreg;
  private Process cameraProcess;

  public ImageServer(ALCRegulator ALCreg) throws IOException {
    System.out.println("FRESH NEW SERVER");
    this.ALCreg = ALCreg;
    String argv = "raspivid -l -o tcp://0.0.0.0:2222 --framerate 10 -w 1270 -h 292 -t 0 --mode 5";
    cameraProcess = Runtime.getRuntime().exec(argv);
  }

  @Override
  public void run() {
    try {
      DatagramSocket serverSocket = new DatagramSocket(9876);
      System.out.println("listening on port 9876");
      byte[] send;
      byte[] receive = new byte[300];

      while (true) {

        DatagramPacket receivePack = new DatagramPacket(receive, receive.length);
        serverSocket.receive(receivePack);
        byte[] message = receivePack.getData();

        int x = java.nio.ByteBuffer.wrap(message).getInt();
        System.out.println("Sending to ALC : " + x);

        ALCreg.calcSteering(x);

        InetAddress IP = receivePack.getAddress();
        int port = receivePack.getPort();

        send = "OK".getBytes(StandardCharsets.UTF_8);
        DatagramPacket sendPack = new DatagramPacket(send, send.length, IP, port);
        serverSocket.send(sendPack);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

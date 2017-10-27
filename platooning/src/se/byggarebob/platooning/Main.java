package se.byggarebob.platooning;

import java.io.IOException;

/**
 * The Class Main.
 */
public class Main {

  /**
   * The main method.
   *
   * @param args the arguments
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InterruptedException the interrupted exception
   */
  public static void main(String args[])
      throws IOException, InterruptedException {
    System.out.println("Waiting for UI to connect...");
    UICom com = new UICom();
    System.out.println("UI Connected starting...");
    Thread comThread = new Thread(com);
    comThread.start();
    comThread.join();
  }
}

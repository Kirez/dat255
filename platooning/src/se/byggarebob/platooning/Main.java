package se.byggarebob.platooning;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 */
public class Main {

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
public class ALC implements Runnable {

  private boolean stopFlagged;

  @Override
  public void run() {
    while (!stopFlagged) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        return;
      }
    }
  }

  public void stop() {
    stopFlagged = true;
  }
}

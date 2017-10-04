public class ACC implements Runnable {

  private boolean stopFlagged = false;

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

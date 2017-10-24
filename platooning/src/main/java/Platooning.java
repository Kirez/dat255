public final class Platooning {

  private CAN can;
  private ACC acc;
  private ALC alc;
  private Thread accThread;
  private Thread alcThread;
  private boolean active;

  public Platooning(CAN can, ACC acc, ALC alc) {
    this.can = can;
    this.acc = acc;
    this.alc = alc;
    accThread = new Thread(acc);
    alcThread = new Thread(alc);
    active = false;
  }

  public void start() {
    if (!active) {
      System.out.println("Starting CAN");
      try {
        can.start();
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.err.println("Failed to start CAN I/O");
        throw new RuntimeException("Failed to start CAN I/O");
      }
      System.out.println("Starting ACC thread");
      accThread.start();
      System.out.println("Starting ALC thread");
      alcThread.start();
    }
    active = true;
  }

  public void stop() {
    if (active) {
      System.out.println("Stopping ACC thread");
      acc.stop();
      System.out.println("Stopping ALC thread");
      alc.stop();
      try {
        alcThread.join(1000);
        accThread.join(1000);
        if (!alcThread.isAlive()) {
          System.out.println("ALC thread stopped");
        } else {
          System.out.println("ALC failed to exit gracefully");
        }
        if (!accThread.isAlive()) {
          System.out.println("ACC thread stopped");
        } else {
          System.out.println("ACC failed to exit gracefully");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        alcThread.interrupt();
        accThread.interrupt();
      }
      try {
        can.sendMotorValue((byte) 0);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Stopping CAN");
      try {
        can.stop();
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.err.println("Failed to stop CAN");
        throw new RuntimeException("Failed to stop CAN");
      }
    }
    active = false;
  }
}
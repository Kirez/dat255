package se.byggarebob.platooning;

// TODO: Auto-generated Javadoc
/**
 * The Class Platooning.
 */
public final class Platooning {

  /** The can. */
  private CAN can;

  /** The acc. */
  private ACC acc;

  /** The acc thread. */
  private Thread accThread;

  /** The alc thread. */
  private Thread alcThread;

  /** The active. */
  private boolean active;

  /**
   * Instantiates a new platooning.
   *
   * @param can the can
   * @param acc the acc
   * @param alc the alc
   */
  public Platooning(CAN can, ACC acc, ALC alc) {
    this.can = can;
    this.acc = acc;
    accThread = new Thread(acc);
    alcThread = new Thread(alc);
    active = false;
  }

  /**
   * Start.
   */
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

  /**
   * Stop.
   */
  public void stop() {
    if (active) {
      System.out.println("Stopping ACC thread");
      acc.stop();
      //System.out.println("Stopping ALC thread");
      //alc.stop();
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
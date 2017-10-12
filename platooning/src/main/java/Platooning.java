import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Platooning {

  private CAN can;
  private ACC acc;
  private ALC alc;
  private Thread accThread;
  private Thread alcThread;
  private boolean active;

  private Platooning(CAN can, ACC acc, ALC alc) {
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
        System.exit(-1);
      }
      System.out.println("Starting ACC thread");
      accThread.start();
      System.out.println("Starting ALC thread");
      alcThread.start();
    }
    active = true;
  }

  private void stop() {
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
      }
      finally {
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
        System.exit(-1);
      }
    }
    active = false;
  }

  public static void main(String args[]) throws IOException, InterruptedException {
    CAN can = CAN.getInstance();
    ServoControl sc = new ServoControl(can);
    ALC alc = new ALC(sc);
    MotorControl mc = new MotorControl(can);
    UltraSonicSensor sensor = new UltraSonicSensor(can);
    ACC acc = new ACC(mc, sensor);

    Platooning platooning = new Platooning(can, acc, alc);
    BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    try {
      System.out.print("Platooning>");
      String line = inputReader.readLine();
      while (line != null) {
        System.out.print("Platooning>");
        String[] tokens = line.split(" ");
        if (tokens.length > 0) {
          if (tokens[0].equals("start")) {
            System.out.println("Starting platooning");
            platooning.start();
          } else if (tokens[0].equals("stop")) {
            System.out.println("Stopping platooning");
            platooning.stop();
            System.out.println("Platooning stopped");
            System.exit(0);
          } else {
            System.out.println("Unknown command " + tokens[0]);
          }
        }
        line = inputReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
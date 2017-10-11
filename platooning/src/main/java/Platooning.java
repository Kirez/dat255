import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Platooning implements Runnable {

  private CAN can;
  private ACC acc;
  private ALC alc;
  private Thread accThread;
  private Thread alcThread; /* private Simulator simulator; //TODO make interchangeable with CAN */

  private Platooning(CAN can, ACC acc, ALC alc) {
    this.can = can;
    this.acc = acc;
    this.alc = alc;
    accThread = new Thread(acc);
    alcThread = new Thread(alc);
  }

  @Override
  public void run() {

    System.out.println("Starting ACC thread");
    accThread.start();
    System.out.println("Starting ALC thread");
    //alcThread.start();

    //TODO Watch for stop condition

    System.out.println("Platooning: waiting for ACC and ALC threads to exit");
    try {
      accThread.join();
      alcThread.join();
      System.out.println("Platooning: ACC and ALC exited");
    } catch (InterruptedException e) {
      e.printStackTrace();
      accThread.interrupt();
      alcThread.interrupt();
    }
  }

  private void stop() {
    System.out.println("Stopping ACC thread");
    acc.stop();
    System.out.println("Stopping ALC thread");
    alc.stop();
    try {
      alcThread.join();
      System.out.println("ALC thread stopped");
      accThread.join();
      System.out.println("ACC thread stopped");
    } catch (InterruptedException e) {
      e.printStackTrace();
      alcThread.interrupt();
      accThread.interrupt();
    }
    try {
      can.sendMotorValue((byte) 0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) throws IOException, InterruptedException {
    System.out.println("Det här är en ny version HEJHEJ");
    CAN can = CAN.getInstance();
    ServoControl sc = new ServoControl(can);
    ALC alc = new ALC(sc); //TODO update when constructor of ALC is done
    MotorControl mc = new MotorControl(can);
    UltraSonicSensor sensor = new UltraSonicSensor(can);
    ACC acc = new ACC(mc, sensor);

    Platooning platooning = new Platooning(can, acc, alc);
    Thread platoonThread = new Thread(platooning);
    BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

    try {
      String line = inputReader.readLine();
      while (line != null) {
        String[] tokens = line.split(" ");
        if (tokens.length > 0) {
          if (tokens[0].equals("start")) {
            System.out.println("Starting platooning thread");
            platoonThread.start();
          } else if (tokens[0].equals("stop")) {
            System.out.println("Stopping platooning thread");
            platooning.stop();
            platoonThread.join();
            System.out.println("Stopped");
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


    //TODO exit
    try {
      platoonThread.interrupt();
      platoonThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


}
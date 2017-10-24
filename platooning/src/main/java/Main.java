import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

  public static void main(String args[]) throws InterruptedException {
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Main {
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
          switch (tokens[0]) {
            case "start":
              System.out.println("Starting platooning");
              platooning.start();
              break;
            case "stop":
              System.out.println("Stopping platooning");
              platooning.stop();
              System.out.println("Platooning stopped");
              System.exit(0);
            default:
              System.out.println("Unknown command " + tokens[0]);
              break;
          }
        }
        line = inputReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

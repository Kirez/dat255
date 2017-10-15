import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UICom implements Runnable {

  private ServerSocket serverSocket;
  private Socket uiSocket;
  private ACC acc;
  private ALC alc;
  private CAN can;
  private MotorControl motorControl;
  private ServoControl servoControl;
  private UltraSonicSensor sensor;
  private Thread accThread;
  private Thread alcThread;

  public enum COMMAND {
    ENABLE_ACC,
    ENABLE_ALC,
    DISABLE_ACC,
    DISABLE_ALC,
    SET_SPEED,
    SET_STEER,
  }

  public UICom() throws IOException {
    serverSocket = new ServerSocket(2221);
    uiSocket = serverSocket.accept();

    can = CAN.getInstance();
    servoControl = new ServoControl(can);
    alc = new ALC(servoControl);
    motorControl = new MotorControl(can);
    sensor = new UltraSonicSensor(can);
    acc = new ACC(motorControl, sensor);
    accThread = new Thread(acc);
    alcThread = new Thread(alc);
  }

  @Override
  public void run() {
    try {
      can.start();
      DataInputStream dataInputStream = new DataInputStream(
          uiSocket.getInputStream());
      while (uiSocket.isConnected()) {
        byte comByte = dataInputStream.readByte();
        COMMAND command = COMMAND.values()[comByte];
        System.out.println(command.toString());
        switch (command) {
          case ENABLE_ACC:
            if (accThread.isAlive()) {
              acc.stop();
              accThread.join(1000);
              accThread.interrupt();
            }
            acc = new ACC(motorControl, sensor);
            accThread = new Thread(acc);
            accThread.start();
            break;
          case ENABLE_ALC:
            if (alcThread.isAlive()) {
              alc.stop();
              alcThread.join(1000);
              alcThread.interrupt();
            }
            alc = new ALC(servoControl);
            alcThread = new Thread(alc);
            alcThread.start();
            break;
          case DISABLE_ACC:
            acc.stop();
            accThread.join(1000);
            accThread.interrupt();
            break;
          case DISABLE_ALC:
            alc.stop();
            alcThread.join(1000);
            alcThread.interrupt();
            break;
          case SET_SPEED:
            byte speed = dataInputStream.readByte();
            System.out.println("Speed: " + speed);
            break;
          case SET_STEER:
            byte steer = dataInputStream.readByte();
            System.out.println("Steer: " + steer);
            break;
        }
      }
      can.stop();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

  }
}

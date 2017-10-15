import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class UICom implements Runnable {

  private ServerSocket serverSocket;
  private Socket uiSocket;
  private ACC acc;
  private ALC alc;
  private CAN can;
  private MotorControl motorControl;
  private ServoControl servoControl;
  private UltraSonicSensor sensor;
  private UIUpdateSender updateSender;
  private Thread accThread;
  private Thread alcThread;
  private Thread uiUpdateThread;

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

    updateSender = new UIUpdateSender(10);
    uiUpdateThread = new Thread(updateSender);
  }

  @Override
  public void run() {
    try {
      can.start();
      DataInputStream dataInputStream = new DataInputStream(
          uiSocket.getInputStream());
      uiUpdateThread.start();

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
            motorControl.setSpeed(speed);
            break;
          case SET_STEER:
            byte steer = dataInputStream.readByte();
            servoControl.steer(steer);
            break;
        }
      }
      updateSender.stopFlagged.set(true);
      uiUpdateThread.join(1000);
      uiUpdateThread.interrupt();
      can.stop();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public class UIUpdateSender implements Runnable {

    public AtomicBoolean stopFlagged;
    private int updateFrequency;

    public UIUpdateSender(int updateFrequency) {
      stopFlagged = new AtomicBoolean(false);
      this.updateFrequency = updateFrequency;
    }

    @Override
    public void run() {
      try {
        while (!stopFlagged.get()) {
          byte speed = can.getMotorValue();
          byte steer = can.getSteerValue();
          byte[] payload = new byte[2];
          payload[0] = speed;
          payload[1] = steer;

          DataOutputStream outputStream = new DataOutputStream(
              uiSocket.getOutputStream());

          outputStream.write(payload);
          Thread.sleep(1000 / updateFrequency);
        }
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }
}

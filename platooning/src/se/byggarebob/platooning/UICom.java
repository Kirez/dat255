package se.byggarebob.platooning;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO: Auto-generated Javadoc
/**
 * The Class UICom.
 */
public class UICom implements Runnable {

  /** The server socket. */
  private ServerSocket serverSocket;

  /** The ui socket. */
  private Socket uiSocket;

  /** The acc. */
  private ACC acc;

  /** The alc. */
  private ALC alc;

  /** The can. */
  private CAN can;

  /** The motor control. */
  private MotorControl motorControl;

  /** The servo control. */
  private ServoControl servoControl;

  /** The sensor. */
  private UltraSonicSensor sensor;

  /** The update sender. */
  private UIUpdateSender updateSender;

  /** The acc thread. */
  private Thread accThread;

  /** The alc thread. */
  private Thread alcThread;

  /** The ui update thread. */
  private Thread uiUpdateThread;

  /**
   * The Enum COMMAND.
   */
  public enum COMMAND {

    /** The enable acc. */
    ENABLE_ACC,

    /** The enable alc. */
    ENABLE_ALC,

    /** The disable acc. */
    DISABLE_ACC,

    /** The disable alc. */
    DISABLE_ALC,

    /** The set speed. */
    SET_SPEED,

    /** The set steer. */
    SET_STEER,
  }

  /**
   * Instantiates a new UI com.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
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

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
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
              //alc.stop();
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
            //alc.stop();
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

  /**
   * The Class UIUpdateSender.
   */
  public class UIUpdateSender implements Runnable {

    /** The stop flagged. */
    public AtomicBoolean stopFlagged;

    /** The update frequency. */
    private int updateFrequency;

    /**
     * Instantiates a new UI update sender.
     *
     * @param updateFrequency the update frequency
     */
    public UIUpdateSender(int updateFrequency) {
      stopFlagged = new AtomicBoolean(false);
      this.updateFrequency = updateFrequency;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Erik Källberg (kalerik@student.chalmers.se)
 * @author Hugo Frost
 *
 * Singleton class CAN for interfacing with can-utils's candump and cansend,
 * mimicking MOPED python code
 */
public final class CAN {

  private static CAN instance;
  private static final String CAN_INTERFACE = "can0";
  private static final String DUMP_COMMAND = "candump";
  private static final String SEND_COMMAND = "cansend";
  private static final String VCU_COMMAND_CAN_ID = "101";
  private static final String VCU_ODOMETER_CAN_ID = "Not known at this time";
  private static final String SCU_ULTRASONIC_CAN_ID = "46C";
  private static byte motorValue = 0;
  private static byte steerValue = 0;
  private static final long VCU_COOL_DOWN = 100; /* TODO find out how fast one can switch command */
  private Thread outputWorkerThread;
  private Thread inputWorkerThread;
  private OutputWorker outputWorker;
  private InputWorker inputWorker;
  private boolean active;

  /**
   * CAN singleton constructor starts CAN input and output worker threads
   */
  private CAN() {
    outputWorker = new OutputWorker();
    outputWorkerThread = new Thread(outputWorker);
    inputWorker = new InputWorker();
    inputWorkerThread = new Thread(inputWorker);
    active = false;
  }

  /**
   * Initiates an instance if one does not exist
   *
   * @return the one and only instance
   */
  public static synchronized CAN getInstance() {
    if (instance == null) {
      instance = new CAN();
    }
    return instance;
  }

  public void start() throws InterruptedException {
    if (active) {
      stop();
      inputWorker = new InputWorker();
      outputWorker = new OutputWorker();
      inputWorkerThread = new Thread(inputWorker);
      outputWorkerThread = new Thread(outputWorker);
    }
    inputWorkerThread.start();
    outputWorkerThread.start();
    active = true;
  }

  public void stop() throws InterruptedException {
    inputWorker.stopFlag.set(true);
    outputWorker.stopFlag.set(true);

    /*Give each thread 1000ms to neatly exit */
    inputWorkerThread.join(1000);
    outputWorkerThread.join(1000);

    /*Send interrupt signal to ensure exit*/
    inputWorkerThread.interrupt();
    outputWorkerThread.interrupt();

    active = false;
  }

  /**
   * Converts each byte to hex code with zero padding
   *
   * @param data byte-string to convert to hex-string
   * @return zero padded hex-string
   */
  private static String byteToHexString(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for (byte b : data) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString();
  }

  /**
   * Schedules a frame to be sent by output worker thread
   *
   * @param frame to be sent
   * @throws InterruptedException from OutputWorker::queueFrame
   */
  private void sendCANFrame(CANFrame frame) throws InterruptedException {
    outputWorker.queueFrame(frame);
  }

  /**
   * Automates sending of motor and steer packets to VCU
   *
   * @param motor value to be sent to VCU
   * @param steer value to be sent to VCU
   * @throws InterruptedException fromSendCANFrame
   */
  public void sendMotorAndSteerValue(byte motor, byte steer)
      throws InterruptedException {
    byte[] motorAndSteerBytes = new byte[2];
    motorAndSteerBytes[0] = motor;
    motorAndSteerBytes[1] = steer;
    CANFrame frame = new CANFrame(VCU_COMMAND_CAN_ID, motorAndSteerBytes);
    sendCANFrame(frame);
    setMotorValue(motor);
    setSteerValue(steer);
  }

  private static void setMotorValue(byte motorValue) {
    CAN.motorValue = motorValue;
  }

  private static void setSteerValue(byte steerValue) {
    CAN.steerValue = steerValue;
  }

  /**
   * Motor and Steer value must be sent at the same time this method sends the
   * old steer value along  with the new motor value
   *
   * @param motor value to be sent to VCU
   * @throws IOException from sendMotorAndSteerValue
   * @throws InterruptedException from sendMotorAndSteerValue
   */
  public void sendMotorValue(byte motor) throws InterruptedException {
    sendMotorAndSteerValue(motor, steerValue);
  }

  /**
   * Motor and Steer value must be sent at the same time this method sends the
   * old motor value along  with the new steer value
   *
   * @param steer value to be sent to VCU
   * @throws IOException from sendMotorAndSteerValue
   * @throws InterruptedException from sendMotorAndSteerValue
   */
  public void sendSteerValue(byte steer) throws InterruptedException {
    sendMotorAndSteerValue(motorValue, steer);
  }

  /**
   * Basically a container class (think C structure) for CAN frames received and
   * sent
   */
  private static class CANFrame {

    private final String identity;
    private final double time;
    private final byte[] data;

    CANFrame(String identity, double time, byte[] data) {
      this.identity = identity;
      this.time = time;
      this.data = data;
    }

    CANFrame(String identity, byte[] data) {
      this(identity, -1, data);
    }

    public String getID() {
      return identity;
    }

    public byte[] getData() {
      return data;
    }

    public double getTime() {
      return time;
    }
  }

  /**
   * Disgusting DistPub-line to sensor reading short-array
   *
   * @return sensor readings
   */
  public Short readSensor() throws InterruptedException {
    String sensorLine = inputWorker.readSensorLine();
    if (sensorLine == null) {
      return null;
    } else {
      try {
        sensorLine = sensorLine.split("\\[")[1];
      } catch (ArrayIndexOutOfBoundsException e) {
        return null;
      }
      sensorLine = sensorLine.trim();
      sensorLine = sensorLine.split("]")[0];

      try {
        return Short.parseShort(sensorLine);
      } catch (NumberFormatException e) {
        System.out.println("Bad sensor data");
      }

      return null;
    }
  }

  /**
   * Runnable, launched by parent (CAN), that reads CAN packets into can frames
   * by launching candump and continuously parsing it's standard output into
   * sensor frames that are put into queues accessible by parent (CAN) object.
   * Uses semaphores for mutex because the java keyword synchronized is
   * confusing
   */
  private static class InputWorker implements Runnable {

    private final Semaphore odometerQueueLock;
    private final Queue<CANFrame> odometerQueue;
    private final Semaphore usSensorQueueLock;
    private final Queue<CANFrame> usSensorQueue;
    private Process canDumpProcess;
    private InputStream canDumpStandardOutput;
    final AtomicBoolean stopFlag;

    InputWorker() {
      odometerQueueLock = new Semaphore(1);
      odometerQueue = new ArrayDeque<>();
      usSensorQueueLock = new Semaphore(1);
      usSensorQueue = new ArrayDeque<>();
      stopFlag = new AtomicBoolean(false);
    }

    /**
     * Beware super lazy parsing. Hardcoded for candump run with flag '-t'
     * option 'z'
     *
     * @return can frame from parsed candump standard output
     */
    private CANFrame readFrame() throws IOException {
      int DATA_OFFSET = 4;
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(canDumpStandardOutput));
      String canDataString = reader.readLine(); /* For example canDataString = "(003.602137) vcan0 535 [8] 04 14 C7 30 3C 96 C5 4B" */
      if (canDataString == null) {
        return null;
      }
      canDataString = canDataString.trim();
      canDataString = canDataString.replace("   ", " "); /* now canDataString = "(003.602137) vcan0 535 [8] 04 14 C7 30 3C 96 C5 4B" */
      canDataString = canDataString.replace("  ", " "); /* now canDataString = "(003.602137) vcan0 535 [8] 04 14 C7 30 3C 96 C5 4B" */
      String[] tokens = canDataString.split(" "); /* now tokens = {"(003.602137)", "vcan0", "558", "[8]", "04", "14", ..., "4B"} */
      String canTimeString = tokens[0].replace("(", "").replace(")", "");
      //String canInterfaceString = tokens[1];
      String canIdString = tokens[2];
      String dataLengthString = tokens[3].replace("[", "").replace("]", "");
      double time = Double.parseDouble(canTimeString);
      int dataLength = Integer.parseInt(dataLengthString);
      byte[] data = new byte[dataLength];
      for (int i = 0; i < dataLength; i++) {
        byte token = (byte) (16 * Character
            .digit(tokens[i + DATA_OFFSET].charAt(0), 16));
        data[i] = token;
        token = (byte) Character.digit(tokens[i + DATA_OFFSET].charAt(1), 16);
        data[i] += token;
      }
      return new CANFrame(canIdString, time, data);
    }

    /**
     * Super-hacky oh-so-ugly DistPub data line re-constructor TODO comment code
     * *
     *
     * @return DistPub data line if available else null
     * @throws InterruptedException if interrupted when waiting for
     * usSensorQueueLock
     */
    private String readSensorLine() throws InterruptedException {
      String line = null;
      usSensorQueueLock.acquire();
      CANFrame frame;
      ArrayList<CANFrame> readFrames = new ArrayList<>(); /* Fast forward to start of next DistPub message */
      while (!usSensorQueue.isEmpty()) {
        frame = usSensorQueue.poll();
        if (frame.data[0] == 0x10) {
          readFrames.add(frame);
          break;
        } else {
          System.out.println(String
              .format("Scrapping frame data: %s", byteToHexString(frame.data)));
        }
      }
      StringBuilder sb = new StringBuilder();
      boolean messageIsComplete = false;
      while (!usSensorQueue.isEmpty()) {
        frame = usSensorQueue.peek();
        if (frame.data[0] == 0x10) {
          messageIsComplete = true;
          break;
        }
        for (int i = 1; i < frame.data.length; i++) {
          sb.append((char) frame.data[i]);
        }
        readFrames.add(usSensorQueue.poll());
      }
      if (!messageIsComplete) {
        usSensorQueue.addAll(readFrames);
      } else {
        line = sb.toString();
      }
      usSensorQueueLock.release();
      return line;
    }

    @Override
    public void run() {
      String[] argv = new String[4];
      argv[0] = CAN.DUMP_COMMAND;
      argv[1] = "-t";
      argv[2] = "z";
      argv[3] = CAN.CAN_INTERFACE;

      try {
        canDumpProcess = Runtime.getRuntime().exec(argv);
      } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Failed to exec: " + String.join(" ", argv));
        return;
      }

      canDumpStandardOutput = canDumpProcess.getInputStream();
      while (!stopFlag.get()) {
        if (!canDumpProcess.isAlive()) {
          System.err.println(
              "candump stopped with code: " + canDumpProcess.exitValue());
          return;
        }
        try {
          CANFrame frame = readFrame();
          if (frame.identity.equals(CAN.VCU_ODOMETER_CAN_ID)) {
            odometerQueueLock.acquire();
            odometerQueue.add(frame);
            odometerQueueLock.release();
          } else if (frame.identity.equals(CAN.SCU_ULTRASONIC_CAN_ID)) {
            usSensorQueueLock.acquire();
            usSensorQueue.add(frame);
            usSensorQueueLock.release();
          } else if (frame.identity.equals(CAN.VCU_COMMAND_CAN_ID)) {
          } else {
            System.out.println(String
                .format("Unknown CAN frame: id=%s; data=%s", frame.identity,
                    byteToHexString(frame.data)));
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
          break;
        }
      }

      canDumpProcess.destroy();
    }
  }

  /**
   * Runnable, launched by parent (CAN), that schedules sending of CAN frames
   * put in queue by parent  When experimenting with the VCU we've found that it
   * ignores commands if they are sent too  quickly. Uses semaphores for shared
   * queues for the same reason as described above InputWorker.
   */
  private static class OutputWorker implements Runnable {

    private final Semaphore queueLock;
    private final Queue<CANFrame> frameOutputQueue;
    final AtomicBoolean stopFlag;

    OutputWorker() {
      queueLock = new Semaphore(1);
      frameOutputQueue = new ArrayDeque<>();
      stopFlag = new AtomicBoolean(false);
    }

    void queueFrame(CANFrame frame) throws InterruptedException {
      queueLock.acquire();
      frameOutputQueue.add(frame);
      queueLock.release();
    }

    private void sendFrame(CANFrame frame)
        throws InterruptedException, IOException {
      String[] argv = new String[3];
      argv[0] = SEND_COMMAND;
      argv[1] = CAN_INTERFACE;
      argv[2] = String
          .format("%s#%s", frame.identity, byteToHexString(frame.data));
      Process csProcess = Runtime.getRuntime().exec(argv);
      csProcess.waitFor();
    }

    @Override
    public void run() {
      while (!stopFlag.get()) {
        try {
          queueLock.acquire();
          if (!frameOutputQueue.isEmpty()) {
            sendFrame(frameOutputQueue.poll());
          }
          queueLock.release();
          Thread.sleep(CAN.VCU_COOL_DOWN);
        } catch (InterruptedException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
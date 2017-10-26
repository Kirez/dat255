package se.byggarebob.platooning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO: Auto-generated Javadoc
/**
 * The Class CAN.
 *
 * @author Erik Källberg (kalerik@student.chalmers.se)
 * @author Hugo Frost
 * <p>
 * Singleton class CAN for interfacing with can-utils's candump and cansend,
 * mimicking MOPED python code.
 */
public final class CAN {

  /** The instance. */
  private static CAN instance;

  /** The can interface. */
  private String CAN_INTERFACE = "can0";

  /** The dump command. */
  private String DUMP_COMMAND = "candump";

  /** The send command. */
  private String SEND_COMMAND = "cansend";

  /** The vcu command can id. */
  private String VCU_COMMAND_CAN_ID = "101";

  /** The vcu odometer can id. */
  private String VCU_ODOMETER_CAN_ID = "Not known at this time";

  /** The scu ultrasonic can id. */
  private String SCU_ULTRASONIC_CAN_ID = "46C";

  /** The motor value. */
  private byte motorValue = 0;

  /** The steer value. */
  private byte steerValue = 0;

  /** The vcu cool down. */
  private long VCU_COOL_DOWN = 100; /* TODO find out how fast one can switch command */

  /** The output worker thread. */
  private Thread outputWorkerThread;

  /** The input worker thread. */
  private Thread inputWorkerThread;

  /** The output worker. */
  private OutputWorker outputWorker;

  /** The input worker. */
  private InputWorker inputWorker;

  /** The active. */
  private boolean active;

  /**
   * CAN singleton constructor starts CAN input and output worker threads.
   */
  private CAN() {
    outputWorker = new OutputWorker();
    outputWorkerThread = new Thread(outputWorker);
    inputWorker = new InputWorker();
    inputWorkerThread = new Thread(inputWorker);
    active = false;
  }

  /**
   * Initiates an instance if one does not exist.
   *
   * @return the one and only instance.
   */
  public synchronized static CAN getInstance() {
    if (instance == null) {
      instance = new CAN();
    }
    return instance;
  }

  /**
   * Converts each byte to hex code with zero padding.
   *
   * @param data byte-string to convert to hex-string.
   * @return zero padded hex-string.
   */
  private static String byteToHexString(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for (byte b : data) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString();
  }

  /**
   * Gets the steer value.
   *
   * @return the steer value
   */
  public byte getSteerValue() {
    return steerValue;
  }

  /**
   * Gets the motor value.
   *
   * @return the motor value
   */
  public byte getMotorValue() {
    return motorValue;
  }

  /**
   * Starts OutputWorker and InputWorker threads.
   *
   * @throws InterruptedException when interrupted whilst stopping old threads.
   */
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

  /**
   * Stops OutputWorker and InputWorker threads.
   *
   * @throws InterruptedException if interrupted while waiting for threads to
   * exit gracefully.
   */
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
   * Automates sending of motor and steer packets to VCU.
   *
   * @param motor value to be sent to VCU.
   * @param steer value to be sent to VCU.
   * @throws InterruptedException the interrupted exception
   */
  public void sendMotorAndSteerValue(byte motor, byte steer)
      throws InterruptedException {
    sendMotorValue(motor);
    sendSteerValue(steer);
  }

  /**
   * Motor and Steer value must be sent at the same time this method sends the
   * old steer value along  with the new motor value.
   *
   * @param motor value to be sent to VCU.
   * @throws InterruptedException the interrupted exception
   */
  public void sendMotorValue(byte motor) throws InterruptedException {
    outputWorker.queueMotorValue(motor);
    motorValue = motor;
  }

  /**
   * Motor and Steer value must be sent at the same time this method sends the
   * old motor value along  with the new steer value.
   *
   * @param steer value to be sent to VCU.
   * @throws InterruptedException from sendMotorAndSteerValue.
   */
  public void sendSteerValue(byte steer) throws InterruptedException {
    outputWorker.queueSteerValue(steer);
    steerValue = steer;
  }

  /**
   * Disgusting DistPub-line to sensor reading short-array.
   *
   * @return sensor readings.
   * @throws InterruptedException the interrupted exception
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
   * Basically a container class (think C structure) for CAN frames received and
   * sent.
   */
  private static class CANFrame {

    /** The identity. */
    private String identity;

    /** The time. */
    private double time;

    /** The data. */
    private byte[] data;

    /**
     * Instantiates a new CAN frame.
     *
     * @param identity the identity
     * @param time the time
     * @param data the data
     */
    public CANFrame(String identity, double time, byte[] data) {
      this.identity = identity;
      this.time = time;
      this.data = data;
    }

    /**
     * Instantiates a new CAN frame.
     *
     * @param identity the identity
     * @param data the data
     */
    public CANFrame(String identity, byte[] data) {
      this(identity, -1, data);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getID() {
      return identity;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public byte[] getData() {
      return data;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public double getTime() {
      return time;
    }
  }

  /**
   * Runnable, launched by parent (CAN), that reads CAN packets into can frames
   * by launching candump and continuously parsing it's standard output into
   * sensor frames that are put into queues accessible by parent (CAN) object.
   * Uses semaphores for mutex because the java keyword synchronized is
   * confusing.
   */
  private class InputWorker implements Runnable {

    /** The stop flag. */
    public AtomicBoolean stopFlag;

    /** The odometer queue lock. */
    private Semaphore odometerQueueLock;

    /** The odometer queue. */
    private Queue<CANFrame> odometerQueue;

    /** The us sensor queue lock. */
    private Semaphore usSensorQueueLock;

    /** The us sensor queue. */
    private Queue<CANFrame> usSensorQueue;

    /** The can dump process. */
    private Process canDumpProcess;

    /** The can dump standard output. */
    private InputStream canDumpStandardOutput;

    /**
     * Instantiates a new input worker.
     */
    public InputWorker() {
      odometerQueueLock = new Semaphore(1);
      odometerQueue = new ArrayDeque<>();
      usSensorQueueLock = new Semaphore(1);
      usSensorQueue = new ArrayDeque<>();
      stopFlag = new AtomicBoolean(false);
    }

    /**
     * Beware super lazy parsing. Hardcoded for candump run with flag '-t'
     * option 'z'.
     *
     * @return can frame from parsed candump standard output.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private CANFrame readFrame() throws IOException {
      int DATA_OFFSET = 4;
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(canDumpStandardOutput, StandardCharsets.UTF_8));
      String canDataString = reader.readLine(); /* For example canDataString = "(003.602137) vcan0 535 [8] 04 14 C7 30 3C 96 C5 4B" */
      if (canDataString != null) {
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
      } else {
        return null;
      }
    }

    /**
     * Super-hacky oh-so-ugly DistPub data line re-constructor. TODO comment
     * code *
     *
     * @return DistPub data line if available else null.
     * @throws InterruptedException if interrupted when waiting for
     * usSensorQueueLock.
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

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      String[] argv = new String[4];
      argv[0] = DUMP_COMMAND;
      argv[1] = "-t";
      argv[2] = "z";
      argv[3] = CAN_INTERFACE;

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
          if (frame.identity.equals(VCU_ODOMETER_CAN_ID)) {
            odometerQueueLock.acquire();
            odometerQueue.add(frame);
            odometerQueueLock.release();
          } else if (frame.identity.equals(SCU_ULTRASONIC_CAN_ID)) {
            usSensorQueueLock.acquire();
            usSensorQueue.add(frame);
            usSensorQueueLock.release();
          } else if (frame.identity.equals(VCU_COMMAND_CAN_ID)) {
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
  private class OutputWorker implements Runnable {

    /** The stop flag. */
    public AtomicBoolean stopFlag;

    /** The queue lock. */
    private Semaphore queueLock;

    /** The frame output queue. */
    private Queue<CANFrame> frameOutputQueue;

    /** The motor queue lock. */
    private Semaphore motorQueueLock;

    /** The steer queue lock. */
    private Semaphore steerQueueLock;

    /** The motor value queue. */
    private Queue<Byte> motorValueQueue;

    /** The steer value queue. */
    private Queue<Byte> steerValueQueue;

    /**
     * Instantiates a new output worker.
     */
    public OutputWorker() {
      queueLock = new Semaphore(1);
      motorQueueLock = new Semaphore(1);
      steerQueueLock = new Semaphore(1);
      frameOutputQueue = new ArrayDeque<>();
      motorValueQueue = new ArrayDeque<>();
      steerValueQueue = new ArrayDeque<>();
      stopFlag = new AtomicBoolean(false);
    }

    /**
     * Put a frame in output queue.
     *
     * @param frame to be entered into queue.
     * @throws InterruptedException if interrupted whilst waiting for queue.
     * lock.
     */
    public void queueFrame(CANFrame frame) throws InterruptedException {
      queueLock.acquire();
      frameOutputQueue.add(frame);
      queueLock.release();
    }

    /**
     * Polls motor and steer value, if any, from their queues and combines them
     * into a VCU command frame.
     *
     * @return VCU frame containing combined value.
     * @throws InterruptedException if interrupted whilst waiting for queue
     * lock.
     */
    private CANFrame getCombinedFrame() throws InterruptedException {
      byte motor, steer;

      boolean motorSet = false;
      boolean steerSet = false;

      motorQueueLock.acquire();

      if (!motorValueQueue.isEmpty()) {
        motor = motorValueQueue.poll();
        motorSet = true;
      } else {
        motor = motorValue;
      }

      motorQueueLock.release();
      steerQueueLock.acquire();

      if (!steerValueQueue.isEmpty()) {
        steer = steerValueQueue.poll();
        steerSet = true;
      } else {
        steer = steerValue;
      }

      steerQueueLock.release();

      if (!motorSet && !steerSet) {
        return null;
      }

      byte[] motorAndSteerBytes = new byte[2];
      motorAndSteerBytes[0] = motor;
      motorAndSteerBytes[1] = steer;
      return new CANFrame(VCU_COMMAND_CAN_ID, motorAndSteerBytes);
    }

    /**
     * Queue motor value to be sent to VCU.
     *
     * @param value to be sent.
     * @throws InterruptedException if interrupted whilst waiting for queue
     * lock.
     */
    public void queueMotorValue(byte value) throws InterruptedException {
      motorQueueLock.acquire();

      motorValueQueue.add(value);

      motorQueueLock.release();
    }

    /**
     * Queue steer value to be sent to VCU.
     *
     * @param value to be sent.
     * @throws InterruptedException if interrupted whilst waiting for queue
     * lock.
     */
    public void queueSteerValue(byte value) throws InterruptedException {
      steerQueueLock.acquire();

      steerValueQueue.add(value);

      steerQueueLock.release();
    }

    /**
     * Sends a can frame using external program cansend.
     *
     * @param frame to be sent.
     * @throws InterruptedException if interrupted before candsend exits.
     * @throws IOException on any I/O exception related to starting cansend.
     */
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

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      while (!stopFlag.get()) {
        try {
          CANFrame combined = getCombinedFrame();
          if (combined != null) {
            queueFrame(combined);
          }
          queueLock.acquire();
          if (!frameOutputQueue.isEmpty()) {
            sendFrame(frameOutputQueue.poll());
          }
          queueLock.release();
          Thread.sleep(VCU_COOL_DOWN);
        } catch (InterruptedException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
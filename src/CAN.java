import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CAN {

  private static CAN instance;
  private static Process cdProcess;
  private static InputStream cdInStream;

  private static String CAN_INTERFACE = "can0";
  private static String DUMP_COMMAND = "candump";
  private static String SEND_COMMAND = "cansend";

  private static byte VCU_CAN_ID = 101;
  private static byte SCU_CAN_ID = -1;
  private static byte motorValue = 0;
  private static byte steerValue = 0;

  /**
   * Singleton constructor for CAN class Starts 'candump' process to listen to interface specified
   * by CAN_INPUT The 'candump' process standard output is setup to be read using InputStream
   * cdInStream
   *
   * @throws IOException when raised by Runtime::exec
   */
  private CAN() throws IOException {
    String[] argv = new String[2];
    argv[0] = DUMP_COMMAND;
    argv[1] = CAN_INTERFACE;
    cdProcess = Runtime.getRuntime().exec(argv);
    cdInStream = cdProcess.getInputStream();
  }

  /**
   * Initiates an instance if one does not exist
   *
   * @return the one and only instance
   * @throws IOException when raised by Runtime::exec
   */
  public static CAN getInstance() throws IOException {
    if (instance == null) {
      instance = new CAN();
    }
    return instance;
  }

  /**
   * Reads a CAN packet by reading a line from standard output of running 'candump' process
   *
   * @return a packet string from running 'candump' process
   */
  public String readCANPacket() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(cdInStream));
    return reader.readLine();
  }

  /**
   * Converts each byte to hex code prefix padded with zeros
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
   * Sends a CAN frame using external program 'cansend'
   *
   * @param canID receivers canID
   * @param data byte-string data
   * @throws IOException when raised by Runtime::exec
   * @throws InterruptedException if interrupted before 'cansend' exits
   */
  public void sendCANFrame(byte canID, byte[] data) throws IOException, InterruptedException {
    String[] argv = new String[3];
    argv[0] = SEND_COMMAND;
    argv[1] = CAN_INTERFACE;
    argv[2] = String.format("%d#%s", canID, byteToHexString(data));
    Process csProcess = Runtime.getRuntime().exec(argv);
    csProcess.waitFor();
  }

  /**
   * Automates sending of motor and steer packets to VCU
   *
   * @param motor value to be sent to VCU
   * @param steer value to be sent to VCU
   * @throws IOException from sendCANFrame
   * @throws InterruptedException fromSendCANFrame
   */
  public void sendMotorAndSteerValue(byte motor, byte steer)
      throws IOException, InterruptedException {
    byte[] motorAndSteerBytes = new byte[2];
    motorAndSteerBytes[0] = motor;
    motorAndSteerBytes[1] = steer;

    sendCANFrame(VCU_CAN_ID, motorAndSteerBytes);

    motorValue = motor;
    steerValue = steer;
  }

  /**
   * Motor and Steer value must be sent at the same time this method sends the old steer value along
   * with the new motor value
   *
   * @param motor value to be sent to VCU
   * @throws IOException from sendMotorAndSteerValue
   * @throws InterruptedException from sendMotorAndSteerValue
   */
  public void sendMotorValue(byte motor) throws IOException, InterruptedException {
    sendMotorAndSteerValue(motor, steerValue);
  }

  /**
   * Motor and Steer value must be sent at the same time this method sends the old motor value along
   * with the new steer value
   *
   * @param steer value to be sent to VCU
   * @throws IOException from sendMotorAndSteerValue
   * @throws InterruptedException from sendMotorAndSteerValue
   */
  public void sendSteerValue(byte steer) throws IOException, InterruptedException {
    sendMotorAndSteerValue(motorValue, steer);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    CAN instance = CAN.getInstance();
    Thread.sleep(1000);
    for (byte i = 0; i < 100; i++) {
      instance.sendMotorAndSteerValue(i, (byte) 0);
    }
  }

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CAN {
  private static CAN instance;
  private static InputStream CANDumpInStream;
  private static String DUMP_COMMAND = "candump vcan0";
  private static Process CANDumpProcess;

  private CAN() throws IOException {
    CANDumpProcess = Runtime.getRuntime().exec(DUMP_COMMAND);
    CANDumpInStream = CANDumpProcess.getInputStream();
  }

  public static CAN getInstance() throws IOException {
    if (instance == null) {
      instance = new CAN();
    }
    return instance;
  }

  public String readCANPacket() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(CANDumpInStream));
    return reader.readLine();
  }

  public static void main(String[] args) throws IOException {
    CAN instance = CAN.getInstance();
    while (true) {
      System.out.println(instance.readCANPacket());
    }
  }

}

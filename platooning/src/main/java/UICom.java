import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UICom implements Runnable {

  private ServerSocket serverSocket;
  private Socket uiSocket;

  public enum COMMAND {
    ENABLE_ACC,
    ENABLE_ALC,
    ENABLE_PLATOONING,
    DISABLE_ACC,
    DISABLE_ALC,
    DISABLE_PLATOONING,
    SET_SPEED,
    SET_STEER,
  }

  public UICom() throws IOException {
    serverSocket = new ServerSocket(2221);
    uiSocket = serverSocket.accept();
  }

  @Override
  public void run() {
    try {
      DataInputStream dataInputStream = new DataInputStream(
          uiSocket.getInputStream());
      while (uiSocket.isConnected()) {
        byte comByte = dataInputStream.readByte();
        COMMAND command = COMMAND.values()[comByte];
        switch (command) {
          case ENABLE_ACC:
            break;
          case ENABLE_ALC:
            break;
          case ENABLE_PLATOONING:
            break;
          case DISABLE_ACC:
            break;
          case DISABLE_ALC:
            break;
          case DISABLE_PLATOONING:
            break;
          case SET_SPEED:
            byte speed = dataInputStream.readByte();
            break;
          case SET_STEER:
            byte steer = dataInputStream.readByte();
            break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

  }
}

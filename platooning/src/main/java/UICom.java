import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UICom implements Runnable {

  private ServerSocket serverSocket;
  private Socket uiSocket;

  public UICom() throws IOException {
    serverSocket = new ServerSocket(2221);
    uiSocket = serverSocket.accept();
  }

  @Override
  public void run() {

  }
}

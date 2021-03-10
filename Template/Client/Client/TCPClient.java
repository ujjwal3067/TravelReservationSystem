package Client;

import java.net.Socket;

public class TCPClient extends Client {
  private static int port;
  private static String hostName;
  private static Socket socket;

  public TCPClient() {
    super();
  }

  @Override
  public void connectServer() {
    try {
      socket = new Socket(hostName, port);
      m_resourceManager = new TCPClientResourceManager(socket);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  public static void main(String[] args) {
    // get port and host name as arguments from console
    if (args.length == 1) {
      String[] host_INFO = args[0].split(",");
      hostName = host_INFO[0];
      port = Integer.parseInt(host_INFO[1]);
    } else {
      System.out.println(" Please specify name and port of the Resource Manager server");
      System.exit(1);
    }

    try {
      TCPClient client = new TCPClient();
      client.connectServer();
      client.start();
    } catch (Exception e) {
      System.err.println((char) 27 + "[31;1mClient exception: " + (char) 27 + "[0mUncaught exception");
      e.printStackTrace();
      System.exit(1);
    }

  }

}

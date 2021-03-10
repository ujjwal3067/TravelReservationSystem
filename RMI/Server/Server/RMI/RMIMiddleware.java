package Server.RMI;

import Server.Interface.*;
import Server.Middleware.*;
import Server.Common.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import java.io.*;
import java.util.*;

public class RMIMiddleware extends Middleware {

  private static String s_serverName = "middleware";
  private static String s_rmiPrefix = "group_06_";

  public RMIMiddleware(String name) {
    // calls the constructor of super class i.e. ResourceManager.java
    super(name);
  }

  /**
   * 
   * method to connect to external servers of type :: serverType ( flight , car
   * ,room )
   * 
   * @param serverType Type of resources.
   * @param server     Server name
   * @param name       Server host
   * @param serverPort port
   */
  protected void connectExternalServers(String servertype, String server, String name, int serverPort) {
    try {
      while (true) {
        try {
          Registry registry = LocateRegistry.getRegistry(name, serverPort);
          switch ((String) servertype) {
            case "flight": {
              // get the flightRMI remote object
              flightRMI = (IResourceManager) registry.lookup(server);
              break;
            }
            case "car": {
              // get the carRMI remote object
              carRMI = (IResourceManager) registry.lookup(server);
              break;
            }
            case "room": {
              // get the roomRMI remote object
              roomRMI = (IResourceManager) registry.lookup(server);
              break;
            }
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
        Thread.sleep(500);
        break;
      }
    } catch (Exception e) {
      Trace.error(e.toString());
      // kill the app
      System.exit(1);
    }

  }

  public static void main(String[] arguments) {

    /**
     * to connect to RM server we need
     * 
     * @param Server name , port number ( where rmiRegistry is listening ), name of
     *               the server ( ServerType)
     */
    if (arguments.length == 3) {
      try {
        // process the input arguements
        String[] flightServer_INFO = arguments[0].split(",");
        String[] carServer_INFO = arguments[1].split(",");
        String[] roomServer_INFO = arguments[2].split(",");

        // populate the server details
        // FLIGHT
        String flightServerName = s_rmiPrefix + flightServer_INFO[0];
        String flightServerHost = flightServer_INFO[1];
        int flightServerPort = Integer.parseInt(flightServer_INFO[2]);
        ArrayList<Object> flightinfo = new ArrayList<>();
        flightinfo.add(flightServerName);
        flightinfo.add(flightServerHost);
        flightinfo.add(flightServerPort);

        // CAR
        String carServerName = s_rmiPrefix + carServer_INFO[0];
        String carServerHost = carServer_INFO[1];
        int carServerPort = Integer.parseInt(carServer_INFO[2]);
        ArrayList<Object> carinfo = new ArrayList<>();
        carinfo.add(carServerName);
        carinfo.add(carServerHost);
        carinfo.add(carServerPort);

        // ROOM
        String roomServerName = s_rmiPrefix + roomServer_INFO[0];
        String roomServerHost = roomServer_INFO[1];
        int roomServerPort = Integer.parseInt(roomServer_INFO[2]);
        ArrayList<Object> roominfo = new ArrayList<>();
        roominfo.add(roomServerName);
        roominfo.add(roomServerHost);
        roominfo.add(roomServerPort);

        // put servers inof into the hashmap
        ServerConfigDetails.put("flight", flightinfo);
        ServerConfigDetails.put("car", carinfo);
        ServerConfigDetails.put("room", roominfo);

      } catch (Exception e) {
        e.printStackTrace();
        // quite the process
        System.exit(1);
      }
    } else {
      // TODO : use trace class instead
      System.err.println(" Please provide 3 Arguements");
      System.exit(1);
    }
    // create Middleware object
    RMIMiddleware middleware = new RMIMiddleware(s_serverName);

    // CREATE the RMI ServerEntry
    try {

      // Dynamically generate the stub ( client proxy )
      IResourceManager resourceManager = (IResourceManager) UnicastRemoteObject.exportObject(middleware, 0);
      // Bind the remote object's stub in the registry
      Registry l_registry;

      // create registry if doesn't exists else locate the existing one
      try {
        l_registry = LocateRegistry.createRegistry(2106);
      } catch (Exception e) {
        // Registry already exists on the port 1106
        l_registry = LocateRegistry.getRegistry(2106);
      }
      final Registry registry = l_registry;
      System.out.println("The RMI middleware Registry: " + l_registry.toString());

      registry.rebind(s_rmiPrefix + s_serverName, resourceManager);
      System.out.println("registry name: " + s_rmiPrefix + s_serverName);
      System.out.println(registry.toString());

      // clean object reference before shutting JVM down
      Runtime.getRuntime().addShutdownHook(new Thread() {

        public void run() {
          try {
            registry.unbind(s_rmiPrefix + s_serverName);
            System.out.println("" + s_serverName + " resource manager unbound");

          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      });
      System.out
          .println("" + s_serverName + " resource manager server read and bound to " + s_rmiPrefix + s_serverName + "");

    } catch (Exception e) {
      // server expception :: cannot connect to server
      e.printStackTrace();
      System.exit(1);
    }

    // creating security manager instance
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }

    // connecting to flightRM external servers
    String server = (String) ServerConfigDetails.get("flight").get(0);
    String hostname = (String) ServerConfigDetails.get("flight").get(1);
    int serverPort = (int) ServerConfigDetails.get("flight").get(2);

    middleware.connectExternalServers("flight", server, hostname, serverPort);

    // connecting to carRM external servers
    server = (String) ServerConfigDetails.get("car").get(0);
    hostname = (String) ServerConfigDetails.get("car").get(1);
    serverPort = (int) ServerConfigDetails.get("car").get(2);

    middleware.connectExternalServers("car", server, hostname, serverPort);

    // connecting to roomRMI external servers
    server = (String) ServerConfigDetails.get("room").get(0);
    hostname = (String) ServerConfigDetails.get("room").get(1);
    serverPort = (int) ServerConfigDetails.get("room").get(2);

    middleware.connectExternalServers("room", server, hostname, serverPort);

  } // end of main
}
// end of RMIMiddleware

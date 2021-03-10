package Server.TCP;

import Server.Common.*;

import java.util.*;
import java.net.*;
import java.io.*;

public class TCPResourceManager extends ResourceManager {
	private static int port; // can set default port to 1066
	private static ServerSocket serverSocket; // socket where server will listen for incoming connection request
	private static InetAddress ipAddress; // IP address of host where this code going to run
	String hostname;

	public TCPResourceManager(String Servername) {
		super(Servername);
	}

	/**
	 * class Clienthandler that creates separate thread for every client request
	 */
	class ClientHandler implements Runnable {
		private Socket socket;
		private BufferedReader input;
		private PrintWriter output;

		public ClientHandler(Socket socket) {
			this.socket = socket;

		}

		public void run() { // ENTRY point of thread
			try {
				output = new PrintWriter(this.socket.getOutputStream(), true);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String inputLine = null;

				while ((inputLine = input.readLine()) != null) {
					String command = inputLine.toLowerCase();
					System.out.println("Command is: " + command);
					String[] inputArguments = command.split(","); // split the input string at ","
					command = inputArguments[0]; // actual command -> add , remove etc..

					ArrayList<String> commandArguments = new ArrayList<String>(); // stores the arguments of the command
					for (int i = 1; i < inputArguments.length; i++) {
						commandArguments.add(inputArguments[i]);
					}

					if (commandArguments.size() == 0) { // if not arguments to command were given
						output.println("error:no argument");// send no output to client/middleware
						continue;
					}
					// TODO : exexute the command here
					String response = null;
					try {
						response = CommandExecutor.execute((ResourceManager) TCPResourceManager.this, command, commandArguments);
					} catch (Exception e) {
						response = "error:" + e.getMessage();
					}
					output.println(response);
				}
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: use Trace.
				System.out.println("error while handling client request");
			}
		}
	}

	public void startSERVER() {
		try {
			serverSocket = new ServerSocket(port, 1, ipAddress);
			System.out.println("Server Listening on port :: " + port);
			System.out.println("IP Address of this host machine :: " + ipAddress);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				Runnable clienthandler = new TCPResourceManager.ClientHandler(clientSocket);
				Thread thread = new Thread(clienthandler);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		// get port and server name as arguments from console
		String serverName = "...";
		try {
			ipAddress = InetAddress.getLocalHost();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		if (args.length == 1) {
			String[] server_INFO = args[0].split(",");
			serverName = server_INFO[0];
			port = Integer.parseInt(server_INFO[1]);
		} else {

			System.out.println(" Please specify name and port of the Resource Manager server");
			System.exit(1);
		}
		TCPResourceManager RM = new TCPResourceManager(serverName);
		RM.startSERVER();

	} // end of main
}

package Client;

import Server.Interface.*;

import java.util.*;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.rmi.ServerException;
import java.rmi.UnmarshalException;

public abstract class Client {
	IResourceManager m_resourceManager = null;

	public Client() {
		super();
	}

	public abstract void connectServer();

	public void start() {
		// Prepare for reading commands
		System.out.println();
		System.out.println("Location \"help\" for list of supported commands");

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			// Read the next command
			String command = "";
			Vector<String> arguments = new Vector<String>();
			try {
				System.out.print((char) 27 + "[32;1m\n>] " + (char) 27 + "[0m");
				command = stdin.readLine().trim();
			} catch (IOException io) {
				System.err.println((char) 27 + "[31;1mClient exception: " + (char) 27 + "[0m" + io.getLocalizedMessage());
				io.printStackTrace();
				System.exit(1);
			}

			try {
				arguments = parse(command);
				Command cmd = Command.fromString((String) arguments.elementAt(0));
				try {
					execute(cmd, arguments);
				} catch (ConnectException e) {
					connectServer();
					execute(cmd, arguments);
				}
			} catch (IllegalArgumentException | ServerException e) {
				System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27 + "[0m" + e.getLocalizedMessage());
			} catch (ConnectException | UnmarshalException e) {
				System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27 + "[0mConnection to server lost");
			} catch (Exception e) {
				System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27 + "[0mUncaught exception");
				e.printStackTrace();
			}
		}
	}

	public String execute(Command cmd, Vector<String> arguments)
			throws RemoteException, NumberFormatException, InvalidTransactionException, TransactionAbortedException {
		switch (cmd) {
			case Help: {
				if (arguments.size() == 1) {
					System.out.println(Command.description());
				} else if (arguments.size() == 2) {
					Command l_cmd = Command.fromString((String) arguments.elementAt(1));
					System.out.println(l_cmd.toString());
				} else {
					System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27
							+ "[0mImproper use of help command. Location \"help\" or \"help,<CommandName>\"");
				}
				return "help";

			}
			case AddFlight: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));
				System.out.println("-Flight Seats: " + arguments.elementAt(3));
				System.out.println("-Flight Price: " + arguments.elementAt(4));

				int id = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));
				int flightSeats = toInt(arguments.elementAt(3));
				int flightPrice = toInt(arguments.elementAt(4));

				if (m_resourceManager.addFlight(id, flightNum, flightSeats, flightPrice)) {
					System.out.println("Flight added");
					return "Flight added";
				} else {
					System.out.println("Flight could not be added");
					return "Flight could not be added";
				}

			}
			case AddCars: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));
				System.out.println("-Number of Cars: " + arguments.elementAt(3));
				System.out.println("-Car Price: " + arguments.elementAt(4));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);
				int numCars = toInt(arguments.elementAt(3));
				int price = toInt(arguments.elementAt(4));

				if (m_resourceManager.addCars(id, location, numCars, price)) {
					System.out.println("Cars added");
					return "Cars added";
				} else {
					System.out.println("Cars could not be added");
					return "Cars could not be added";
				}

			}
			case AddRooms: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));
				System.out.println("-Number of Rooms: " + arguments.elementAt(3));
				System.out.println("-Room Price: " + arguments.elementAt(4));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);
				int numRooms = toInt(arguments.elementAt(3));
				int price = toInt(arguments.elementAt(4));

				if (m_resourceManager.addRooms(id, location, numRooms, price)) {
					System.out.println("Rooms added");
					return "Rooms added";
				} else {
					System.out.println("Rooms could not be added");
					return "Rooms could not be added";
				}

			}
			case AddCustomer: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

				int id = toInt(arguments.elementAt(1));
				int customer = m_resourceManager.newCustomer(id);

				System.out.println("Add customer ID: " + customer);
				return Integer.toString(customer);

			}
			case AddCustomerID: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));

				if (m_resourceManager.newCustomer(id, customerID)) {
					System.out.println("Add customer ID: " + customerID);
					return Integer.toString(customerID);
				} else {
					System.out.println("Customer could not be added");
					return "Customer could not be added";
				}

			}
			case DeleteFlight: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));

				if (m_resourceManager.deleteFlight(id, flightNum)) {
					System.out.println("Flight Deleted");
					return "Flight Deleted";
				} else {
					System.out.println("Flight could not be deleted");
					return "Flight could not be deleted";
				}

			}
			case DeleteCars: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				if (m_resourceManager.deleteCars(id, location)) {
					System.out.println("Cars Deleted");
					return "Cars Deleted";
				} else {
					System.out.println("Cars could not be deleted");
					return "Cars could not be deleted";
				}

			}
			case DeleteRooms: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				if (m_resourceManager.deleteRooms(id, location)) {
					System.out.println("Rooms Deleted");
					return "Rooms Deleted";
				} else {
					System.out.println("Rooms could not be deleted");
					return "Rooms could not be deleted";
				}

			}
			case DeleteCustomer: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting a customer from the database [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));

				if (m_resourceManager.deleteCustomer(id, customerID)) {
					System.out.println("Customer Deleted");
					return "Customer Deleted";
				} else {
					System.out.println("Customer could not be deleted");
					return "Customer could not be deleted";
				}

			}
			case QueryFlight: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));

				int seats = m_resourceManager.queryFlight(id, flightNum);
				System.out.println("Number of seats available: " + seats);
				return Integer.toString(seats);

			}
			case QueryCars: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				int numCars = m_resourceManager.queryCars(id, location);
				System.out.println("Number of cars at this location: " + numCars);
				return Integer.toString(numCars);

			}
			case QueryRooms: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				int numRoom = m_resourceManager.queryRooms(id, location);
				System.out.println("Number of rooms at this location: " + numRoom);
				return Integer.toString(numRoom);

			}
			case QueryCustomer: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));

				String bill = m_resourceManager.queryCustomerInfo(id, customerID);
				System.out.print(bill);
				return bill;

			}
			case QueryFlightPrice: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));

				int price = m_resourceManager.queryFlightPrice(id, flightNum);
				System.out.println("Price of a seat: " + price);
				return Integer.toString(price);

			}
			case QueryCarsPrice: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				int price = m_resourceManager.queryCarsPrice(id, location);
				System.out.println("Price of cars at this location: " + price);
				return Integer.toString(price);

			}
			case QueryRoomsPrice: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));

				int id = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);

				int price = m_resourceManager.queryRoomsPrice(id, location);
				System.out.println("Price of rooms at this location: " + price);
				return Integer.toString(price);

			}
			case ReserveFlight: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Flight Number: " + arguments.elementAt(3));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));
				int flightNum = toInt(arguments.elementAt(3));

				if (m_resourceManager.reserveFlight(id, customerID, flightNum)) {
					System.out.println("Flight Reserved");
					return "Flight Reserved";
				} else {
					System.out.println("Flight could not be reserved");
					return "Flight could not be reserved";
				}

			}
			case ReserveCar: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Car Location: " + arguments.elementAt(3));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));
				String location = arguments.elementAt(3);

				if (m_resourceManager.reserveCar(id, customerID, location)) {
					System.out.println("Car Reserved");
					return "Car Reserved";
				} else {
					System.out.println("Car could not be reserved");
					return "Car could not be reserved";
				}

			}
			case ReserveRoom: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Room Location: " + arguments.elementAt(3));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));
				String location = arguments.elementAt(3);

				if (m_resourceManager.reserveRoom(id, customerID, location)) {
					System.out.println("Room Reserved");
					return "Room Reserved";
				} else {
					System.out.println("Room could not be reserved");
					return "Room could not be reserved";
				}

			}
			case Bundle: {
				if (arguments.size() < 7) {
					System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27
							+ "[0mBundle command expects at least 7 arguments. Location \"help\" or \"help,<CommandName>\"");
					break;
				}

				System.out.println("Reserving an bundle [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				for (int i = 0; i < arguments.size() - 6; ++i) {
					System.out.println("-Flight Number: " + arguments.elementAt(3 + i));
				}
				System.out.println("-Location for Car/Room: " + arguments.elementAt(arguments.size() - 3));
				System.out.println("-Book Car: " + arguments.elementAt(arguments.size() - 2));
				System.out.println("-Book Room: " + arguments.elementAt(arguments.size() - 1));

				int id = toInt(arguments.elementAt(1));
				int customerID = toInt(arguments.elementAt(2));
				Vector<String> flightNumbers = new Vector<String>();
				for (int i = 0; i < arguments.size() - 6; ++i) {
					flightNumbers.addElement(arguments.elementAt(3 + i));
				}
				String location = arguments.elementAt(arguments.size() - 3);
				boolean car = toBoolean(arguments.elementAt(arguments.size() - 2));
				boolean room = toBoolean(arguments.elementAt(arguments.size() - 1));

				if (m_resourceManager.bundle(id, customerID, flightNumbers, location, car, room)) {
					System.out.println("Bundle Reserved");
					return "Bundle Reserved";
				} else {
					System.out.println("Bundle could not be reserved");
					return "Bundle could not be reserved";
				}

			}

			case Start: {
				checkArgumentsCount(1, arguments.size());
				System.out.println("<< Transaction started >>");
				// System.out.println(color.YELLOW + "<< Transaction started >>");
				int xid = m_resourceManager.Start();
				// System.out.println(color.RED + "Transaction <xid> : " + xid);
				System.out.println("Transaction <xid> : " + xid);
				return Integer.toString(xid);

			}
			case Commit: {
				checkArgumentsCount(2, arguments.size());
				int xid = toInt(arguments.elementAt(1));
				boolean isCommitted = m_resourceManager.commit(xid);
				if (isCommitted == true) {
					// System.out.println(color.GREEN + "<xid> " + xid + " Commited");
					System.out.println("<xid> " + xid + " Committed");
					return "<xid> " + xid + " Committed";
				} else {
					// System.out.println(color.GREEN + "<xid> " + xid + " Aborted");
					System.out.println("<xid> " + xid + " Aborted");
					return "<xid> " + xid + " Aborted";
				}

			}
			case Abort: {
				checkArgumentsCount(2, arguments.size());
				int xid = toInt(arguments.elementAt(1));
				m_resourceManager.abort(xid);
				System.out.println("<xid> " + xid + ": Aborted");
				return "<xid> " + xid + ": Aborted";

			}
			case Quit:
				checkArgumentsCount(1, arguments.size());

				System.out.println("Quitting client");
				return "Quitting client";
			// System.exit(0);
		}
		return "Invalid command";
	}

	public static Vector<String> parse(String command) {
		Vector<String> arguments = new Vector<String>();
		StringTokenizer tokenizer = new StringTokenizer(command, ",");
		String argument = "";
		while (tokenizer.hasMoreTokens()) {
			argument = tokenizer.nextToken();
			argument = argument.trim();
			arguments.add(argument);
		}
		return arguments;
	}

	public static void checkArgumentsCount(Integer expected, Integer actual) throws IllegalArgumentException {
		if (expected != actual) {
			throw new IllegalArgumentException("Invalid number of arguments. Expected " + (expected - 1) + ", received "
					+ (actual - 1) + ". Location \"help,<CommandName>\" to check usage of this command");
		}
	}

	public static int toInt(String string) throws NumberFormatException {
		return (Integer.valueOf(string)).intValue();
	}

	public static boolean toBoolean(String string)// throws Exception
	{
		return (Boolean.valueOf(string)).booleanValue();
	}
}

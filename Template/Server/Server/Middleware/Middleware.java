package Server.Middleware;

import Server.Interface.*;
import java.util.*;
import java.util.List;
import java.rmi.RemoteException;
import java.util.Vector;

public class Middleware implements IResourceManager {
	protected String m_name;
	protected IResourceManager flightRMI = null;
	protected IResourceManager carRMI = null;
	protected IResourceManager roomRMI = null;

	protected static HashMap<String, ArrayList<Object>> ServerConfigDetails = new HashMap<>();

	public Middleware(String name) {
		m_name = name;
	}

	@Override
	public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
		return flightRMI.addFlight(id, flightNum, flightSeats, flightPrice);
	}

	@Override
	public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
		return carRMI.addCars(id, location, numCars, price);
	}

	@Override
	public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
		return roomRMI.addRooms(id, location, numRooms, price);
	}

	@Override
	public int newCustomer(int id) throws RemoteException {
		int cid = flightRMI.newCustomer(id);

		if (!carRMI.newCustomer(id, cid)) {
			throw new RemoteException("Customer id collision.");
		}
		if (!roomRMI.newCustomer(id, cid)) {
			throw new RemoteException("Customer id collision.");
		}
		return cid;
	}

	@Override
	public boolean newCustomer(int id, int cid) throws RemoteException {
		boolean resFlight = flightRMI.newCustomer(id, cid);
		boolean resCar = carRMI.newCustomer(id, cid);
		boolean resRoom = roomRMI.newCustomer(id, cid);
		return resFlight && resCar && resRoom;
	}

	@Override
	public boolean deleteFlight(int id, int flightNum) throws RemoteException {
		return flightRMI.deleteFlight(id, flightNum);
	}

	@Override
	public boolean deleteCars(int id, String location) throws RemoteException {
		return carRMI.deleteCars(id, location);
	}

	@Override
	public boolean deleteRooms(int id, String location) throws RemoteException {
		return roomRMI.deleteRooms(id, location);
	}

	@Override
	public boolean deleteCustomer(int id, int customerID) throws RemoteException {
		boolean resFlight = flightRMI.deleteCustomer(id, customerID);
		boolean resCar = carRMI.deleteCustomer(id, customerID);
		boolean resRoom = roomRMI.deleteCustomer(id, customerID);
		return resFlight && resCar && resRoom;
	}

	@Override
	public int queryFlight(int id, int flightNumber) throws RemoteException {
		return flightRMI.queryFlight(id, flightNumber);
	}

	@Override
	public int queryCars(int id, String location) throws RemoteException {
		return carRMI.queryCars(id, location);
	}

	@Override
	public int queryRooms(int id, String location) throws RemoteException {
		return roomRMI.queryRooms(id, location);
	}

	@Override
	public String queryCustomerInfo(int id, int customerID) throws RemoteException {
		String[] flightInfo = flightRMI.queryCustomerInfo(id, customerID).split("\n");
		String[] roomInfo = roomRMI.queryCustomerInfo(id, customerID).split("\n");
		String[] carInfo = carRMI.queryCustomerInfo(id, customerID).split("\n");
		List<String> infoLines = new ArrayList<>();
		for (String singleFlightInfo : flightInfo) {
			infoLines.add(singleFlightInfo);
		}
		for (int i = 1; i < roomInfo.length; i++) {
			infoLines.add(roomInfo[i]);
		}
		for (int i = 1; i < carInfo.length; i++) {
			infoLines.add(carInfo[i]);
		}
		return String.join("\n", infoLines);

	}

	@Override
	public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
		return flightRMI.queryFlightPrice(id, flightNumber);
	}

	@Override
	public int queryCarsPrice(int id, String location) throws RemoteException {
		return carRMI.queryCarsPrice(id, location);
	}

	@Override
	public int queryRoomsPrice(int id, String location) throws RemoteException {
		return roomRMI.queryRoomsPrice(id, location);
	}

	@Override
	public boolean reserveFlight(int id, int customerID, int flightNumber) throws RemoteException {
		return flightRMI.reserveFlight(id, customerID, flightNumber);
	}

	@Override
	public boolean reserveCar(int id, int customerID, String location) throws RemoteException {
		return carRMI.reserveCar(id, customerID, location);
	}

	@Override
	public boolean reserveRoom(int id, int customerID, String location) throws RemoteException {
		return roomRMI.reserveRoom(id, customerID, location);
	}

	@Override
	public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car,
			boolean room) throws RemoteException {
		if (flightNumbers.size() < 1) {
			throw new RemoteException();
		}
		boolean result = true;
		for (String flightNumber : flightNumbers) {
			result &= reserveFlight(id, customerID, Integer.parseInt(flightNumber));
		}
		if (car) {
			result &= reserveCar(id, customerID, location);
		}
		if (room) {
			result &= reserveRoom(id, customerID, location);
		}
		return result;
	}

	@Override
	public String getName() throws RemoteException {
		return m_name;
	}

}

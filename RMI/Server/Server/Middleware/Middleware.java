package Server.Middleware;

import Server.Common.*;
import Server.Interface.*;
import Server.LockManager.*;
import java.util.*;
import java.util.List;
import java.rmi.*;
import java.util.Vector;

public class Middleware implements IResourceManager {
	protected String m_name;
	protected IResourceManager flightRMI = null;
	protected IResourceManager carRMI = null;
	protected IResourceManager roomRMI = null;

	protected LockManager lockManager;
	protected TransactionManager transactionManager;
	// lock types
	TransactionLockObject.LockType LOCK_WRITE = TransactionLockObject.LockType.LOCK_WRITE;
	TransactionLockObject.LockType LOCK_READ = TransactionLockObject.LockType.LOCK_READ;

	protected static HashMap<String, ArrayList<Object>> ServerConfigDetails = new HashMap<>();

	public Middleware(String name) {
		m_name = name;
		// start new thread for transaction manager
		this.transactionManager = new TransactionManager(this);
		lockManager = new LockManager();
	}

	/**
	 * Boots up the transactionManager
	 */
	public int Start() throws RemoteException {
		int xid = transactionManager.start(); // start the transaction + create first transaction with xid = 1
		return xid; // return the xid to the client
	}

	public void addTransaction(int xid) throws RemoteException {
		if (!this.transactionManager.isActive(xid)) {
			this.transactionManager.addTransaction(xid, new Transaction(xid));
		}
	}

	/**
	 * Checks if given transaction is valid or not
	 * 
	 * @param xid
	 * @throws InvalidTransactionException
	 * @throws TransactionAbortedException
	 */
	public void isValidTransaction(int xid) throws InvalidTransactionException, TransactionAbortedException {
		if (transactionManager.getTransaction(xid) != null) { // if the transaction is active
			transactionManager.getTransaction(xid).updateTimeStamp(); // update the timeStamp of the transaction
		} else {
			throw new TransactionAbortedException(xid);
		}
		return;
	}

	/**
	 * Methods aborts a given transaction and removes it from the activeTransaction
	 * list in transactionManager
	 * 
	 * @param xid
	 */
	public void abort(int xid) throws RemoteException, InvalidTransactionException {
		System.out.println("<< Aborting  Transaction >> " + "xid : " + xid);
		try {
			isValidTransaction(xid); // will throw exception if xid if not active transaction
		} catch (TransactionAbortedException e) {
			throw new InvalidTransactionException(xid); // transaction already aborted
		}
		Transaction transaction = transactionManager.getTransaction(xid); // get the transaction
		Set<String> resourceManagersObjects = transaction.getRM();
		if (resourceManagersObjects.contains("Flight")) { // if transaction used FLight Object
			flightRMI.abort(xid); // abort xid transaction
		} else if (resourceManagersObjects.contains(("Car"))) {
			carRMI.abort(xid); // abort xid transaction
		} else if (resourceManagersObjects.contains("Room")) {
			roomRMI.abort(xid);
		}
		this.transactionManager.addTransaction(xid, null);
		this.lockManager.UnlockAll(xid);

	}

	/**
	 * Commits the given transaction on the main server
	 * 
	 * @param xid
	 */
	public boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
		isValidTransaction(xid);
		Transaction transaction = transactionManager.getTransaction(xid);
		Set<String> RM_resources = transaction.getRM();

		// Commit to all RM if any operation happens on it. Customer happens on all RM.
		if (RM_resources.contains("Customer") || RM_resources.contains("Flight")) {
			flightRMI.commit(xid);
		}
		if (RM_resources.contains("Customer") || RM_resources.contains("Car")) {
			carRMI.commit(xid);
		}
		if (RM_resources.contains("Customer") || RM_resources.contains("Room")) {
			roomRMI.commit(xid);
		}
		this.transactionManager.addTransaction(xid, null); // remove the xid from active tranaction list
		this.lockManager.UnlockAll(xid); // release all the held locks
		return true;

	}

	protected void getLock(int xid, String item, TransactionLockObject.LockType type)
			throws RemoteException, TransactionAbortedException, InvalidTransactionException {
		try {
			boolean LOCK = this.lockManager.Lock(xid, item, type);
			if (LOCK == false) {
				throw new InvalidTransactionException(xid);
			}
		} catch (DeadlockException e) {
			abort(xid); // abort the transaction if there is deadlock
			throw new TransactionAbortedException(xid); // TODO : add message to exception
		}

	}

	/**
	 * updates the time stamp of the transaction to avoid timeout
	 * 
	 * @param xid
	 */
	public void updateTimeStamp(int xid) {
		this.transactionManager.getTransaction(xid).updateTimeStamp();
	}

	/**
	 * 
	 * Associates a transaction to a object
	 * 
	 * @param xid
	 * @param RM  :: Flight, Car, Room type
	 */
	protected void attachTransactionWithRM(int xid, String RM) throws RemoteException {
		Transaction transaction = this.transactionManager.getTransaction(xid);
		transaction.addRM(RM); // add the RM object to transaction obeject list
		switch (RM) {
			case "Flight": {
				this.flightRMI.addTransaction(xid);
				break;
			}
			case "Car": {
				this.carRMI.addTransaction(xid);
				break;
			}
			case "Room": {
				this.roomRMI.addTransaction(xid);
				break;
			}
			default: {
				throw new RemoteException("Invalid RM.");
			}
			// TODO : deal with customer object
		}

	}

	@Override
	public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Flight.getKey(flightNum), LOCK_WRITE);
		attachTransactionWithRM(xid, "Flight");
		System.out.println("transaction is valid.");
		return flightRMI.addFlight(xid, flightNum, flightSeats, flightPrice);
	}

	@Override
	public boolean addCars(int xid, String location, int numCars, int price)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Car.getKey(location), LOCK_WRITE);
		attachTransactionWithRM(xid, "Car");
		return carRMI.addCars(xid, location, numCars, price);
	}

	@Override
	public boolean addRooms(int xid, String location, int numRooms, int price)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Room.getKey(location), LOCK_WRITE);
		attachTransactionWithRM(xid, "Room");
		return roomRMI.addRooms(xid, location, numRooms, price);
	}

	@Override
	public int newCustomer(int xid) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		// TODO: query 2 following RMs before creating new customer to ensure success.
		isValidTransaction(xid);
		attachTransactionWithRM(xid, "Flight");
		attachTransactionWithRM(xid, "Car");
		attachTransactionWithRM(xid, "Room");

		int cid = flightRMI.newCustomer(xid);
		getLock(xid, Customer.getKey(cid), LOCK_WRITE);

		if (!carRMI.newCustomer(xid, cid)) {
			throw new RemoteException("Customer id collision.");
		}
		if (!roomRMI.newCustomer(xid, cid)) {
			throw new RemoteException("Customer id collision.");
		}
		return cid;
	}

	@Override
	public boolean newCustomer(int xid, int cid)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		// TODO: query all RMs before creating new customer to ensure success.
		isValidTransaction(xid);
		getLock(xid, Customer.getKey(cid), LOCK_WRITE);
		attachTransactionWithRM(xid, "Flight");
		attachTransactionWithRM(xid, "Car");
		attachTransactionWithRM(xid, "Room");

		boolean resFlight = flightRMI.newCustomer(xid, cid);
		boolean resCar = carRMI.newCustomer(xid, cid);
		boolean resRoom = roomRMI.newCustomer(xid, cid);
		return resFlight && resCar && resRoom;
	}

	@Override
	public boolean deleteFlight(int xid, int flightNum)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Flight.getKey(flightNum), LOCK_WRITE);
		attachTransactionWithRM(xid, "Flight");
		return flightRMI.deleteFlight(xid, flightNum);
	}

	@Override
	public boolean deleteCars(int xid, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Car.getKey(location), LOCK_WRITE);
		attachTransactionWithRM(xid, "Car");
		return carRMI.deleteCars(xid, location);
	}

	@Override
	public boolean deleteRooms(int xid, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Room.getKey(location), LOCK_WRITE);
		attachTransactionWithRM(xid, "Room");
		return roomRMI.deleteRooms(xid, location);
	}

	@Override
	public boolean deleteCustomer(int xid, int cid)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Customer.getKey(cid), LOCK_WRITE);
		attachTransactionWithRM(xid, "Flight");
		attachTransactionWithRM(xid, "Car");
		attachTransactionWithRM(xid, "Room");

		boolean resFlight = flightRMI.deleteCustomer(xid, cid);
		boolean resCar = carRMI.deleteCustomer(xid, cid);
		boolean resRoom = roomRMI.deleteCustomer(xid, cid);
		return resFlight && resCar && resRoom;
	}

	@Override
	public int queryFlight(int xid, int flightNumber)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Flight.getKey(flightNumber), LOCK_READ);
		attachTransactionWithRM(xid, "Flight");
		return flightRMI.queryFlight(xid, flightNumber);
	}

	@Override
	public int queryCars(int xid, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Car.getKey(location), LOCK_READ);
		attachTransactionWithRM(xid, "Car");
		return carRMI.queryCars(xid, location);
	}

	@Override
	public int queryRooms(int xid, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Room.getKey(location), LOCK_READ);
		attachTransactionWithRM(xid, "Room");
		return roomRMI.queryRooms(xid, location);
	}

	@Override
	public String queryCustomerInfo(int xid, int customerID)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Customer.getKey(customerID), LOCK_WRITE);
		attachTransactionWithRM(xid, "Flight");
		attachTransactionWithRM(xid, "Car");
		attachTransactionWithRM(xid, "Room");

		String[] flightInfo = flightRMI.queryCustomerInfo(xid, customerID).split("\n");
		String[] roomInfo = roomRMI.queryCustomerInfo(xid, customerID).split("\n");
		String[] carInfo = carRMI.queryCustomerInfo(xid, customerID).split("\n");
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
	public int queryFlightPrice(int xid, int flightNumber)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Flight.getKey(flightNumber), LOCK_READ);
		attachTransactionWithRM(xid, "Flight");
		return flightRMI.queryFlightPrice(xid, flightNumber);
	}

	@Override
	public int queryCarsPrice(int xid, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Car.getKey(location), LOCK_READ);
		attachTransactionWithRM(xid, "Car");
		return carRMI.queryCarsPrice(xid, location);
	}

	@Override
	public int queryRoomsPrice(int xid, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Room.getKey(location), LOCK_READ);
		attachTransactionWithRM(xid, "Room");
		return roomRMI.queryRoomsPrice(xid, location);
	}

	@Override
	public boolean reserveFlight(int xid, int customerID, int flightNumber)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Flight.getKey(flightNumber), LOCK_WRITE);
		attachTransactionWithRM(xid, "Flight");
		return flightRMI.reserveFlight(xid, customerID, flightNumber);
	}

	@Override
	public boolean reserveCar(int xid, int customerID, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Car.getKey(location), LOCK_WRITE);
		attachTransactionWithRM(xid, "Car");
		return carRMI.reserveCar(xid, customerID, location);
	}

	@Override
	public boolean reserveRoom(int xid, int customerID, String location)
			throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		isValidTransaction(xid);
		getLock(xid, Room.getKey(location), LOCK_WRITE);
		attachTransactionWithRM(xid, "Room");
		return roomRMI.reserveRoom(xid, customerID, location);
	}

	/**
	 * Bundle operation. 1. Acquires all locks in advance, will hold the locks even
	 * the operation is not successful. 2. Query all required resources before
	 * making reservations to ensure atomicity.
	 */
	@Override
	public boolean bundle(int xid, int customerID, Vector<String> flightNumbers, String location, boolean car,
			boolean room) throws RemoteException, InvalidTransactionException, TransactionAbortedException {
		if (flightNumbers.size() < 1) {
			throw new RemoteException("Invalid arguments for bundle operation.");
		}

		isValidTransaction(xid);
		for (String flightNumber : flightNumbers) {
			getLock(xid, Flight.getKey(Integer.parseInt(flightNumber)), LOCK_WRITE);
			int numberOfFlights = queryFlight(xid, Integer.parseInt(flightNumber));
			if (numberOfFlights == 0) {
				return false;
			}
			attachTransactionWithRM(xid, "Flight");
		}
		if (car) {
			getLock(xid, Car.getKey(location), LOCK_WRITE);
			int numberOfCars = queryCars(xid, location);
			if (numberOfCars == 0) {
				return false;
			}
			attachTransactionWithRM(xid, "Car");
		}
		if (room) {
			getLock(xid, Room.getKey(location), LOCK_WRITE);
			int numberOfRooms = queryRooms(xid, location);
			if (numberOfRooms == 0) {
				return false;
			}
			attachTransactionWithRM(xid, "Room");
		}

		boolean result = true;
		for (String flightNumber : flightNumbers) {
			result &= reserveFlight(xid, customerID, Integer.parseInt(flightNumber));
		}
		if (car) {
			result &= reserveCar(xid, customerID, location);
		}
		if (room) {
			result &= reserveRoom(xid, customerID, location);
		}
		return result;
	}

	@Override
	public String getName() throws RemoteException {
		return m_name;
	}

}

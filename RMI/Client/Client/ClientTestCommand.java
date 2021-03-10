package Client;

import java.util.*;

public class ClientTestCommand {

  public Vector<String> writeFlight(String xid, int flightNumber, int numberOfSeats, int pricePerSeat) {

    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("addFlight");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(flightNumber));
    commandArgs.add(Integer.toString(numberOfSeats));
    commandArgs.add(Integer.toString(pricePerSeat));

    return commandArgs;
  }

  public Vector<String> writeRoom(String xid, int location, int numRoom, int price) {

    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("addRooms");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(location));
    commandArgs.add(Integer.toString(numRoom));
    commandArgs.add(Integer.toString(price));

    return commandArgs;
  }

  public Vector<String> writeCar(String xid, int location, int numberOfCars, int price) {

    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("addCars");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(location));
    commandArgs.add(Integer.toString(numberOfCars));
    commandArgs.add(Integer.toString(price));

    return commandArgs;
  }

  public Vector<String> writeCustomer(String xid) {

    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("addCustomer");
    commandArgs.add(xid);

    return commandArgs;
  }

  public Vector<String> readFlight(String xid, int flightNumber) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("queryFlight");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(flightNumber));

    return commandArgs;

  }

  public Vector<String> readCars(String xid, int location) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("queryCars");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(location));

    return commandArgs;

  }

  public Vector<String> readRooms(String xid, int location) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("queryRooms");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(location));

    return commandArgs;
  }

  public Vector<String> reserveFlight(String xid, int flightNumber) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("queryFlight");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(flightNumber));

    return commandArgs;
  }

  public Vector<String> reserveCars(String xid, int location) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("reserveCars");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(location));

    return commandArgs;
  }

  public Vector<String> reserveRooms(String xid, int location) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("reserveRooms");
    commandArgs.add(xid);
    commandArgs.add(Integer.toString(location));

    return commandArgs;
  }

  public Vector<String> start() {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("start");

    return commandArgs;
  }

  public Vector<String> commit(String xid) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("commit");
    commandArgs.add(xid);

    return commandArgs;
  }

  public Vector<String> abort(String xid) {
    Vector<String> commandArgs = new Vector<>();

    commandArgs.add("abort");
    commandArgs.add(xid);

    return commandArgs;
  }

}

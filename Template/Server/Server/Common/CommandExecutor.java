package Server.Common;

import java.util.*;

public class CommandExecutor {

  /**
   * Method responsible for executing commands
   */
  public static String execute(ResourceManager RM, String command, ArrayList<String> arguments)
      throws IllegalArgumentException {
    String commandtype = command.toLowerCase();
    try {
      switch (commandtype) {

        /**
         * Add operation first
         */
        case "addflight": {
          // parsing the commands for the "AddFlight" command
          int xid = Integer.parseInt(arguments.get(0));
          int flightNum = Integer.parseInt(arguments.get(1));
          int flightSeats = Integer.parseInt(arguments.get(2));
          int flightPrice = Integer.parseInt(arguments.get(3));
          boolean success = RM.addFlight(xid, flightNum, flightSeats, flightPrice);
          if (success == true) { // operation was successful
            return "true";
          } else {
            return "false";
          }

        }

        case "addcars": {
          // parsing the commands for the "AddCars" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          int carCount = Integer.parseInt(arguments.get(2));
          int carPrice = Integer.parseInt(arguments.get(3));
          boolean success = RM.addCars(xid, location, carCount, carPrice);
          return success ? "true" : "false";
        }

        case "addrooms": {
          // parsing the commands for the "AddRomms" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          int roomCount = Integer.parseInt(arguments.get(2));
          int roomPrice = Integer.parseInt(arguments.get(3));
          boolean success = RM.addRooms(xid, location, roomCount, roomPrice);
          return success ? "true" : "false";
        }

        case "addcustomer": {
          // parsing the commands for the "AddCustomer" command
          int xid = Integer.parseInt(arguments.get(0));
          int customerID = RM.newCustomer(xid);
          return Integer.toString(customerID);
        }

        case "addcustomerid": {
          // parsing the commands for the "AddCustomerID" command
          int xid = Integer.parseInt(arguments.get(0));
          int customerID = Integer.parseInt(arguments.get(1));
          boolean success = RM.newCustomer(xid, customerID);
          return success ? "true" : "false";
        }

        case "deleteflight": {
          // parsing the commands for the "DeleteFlight" command
          int xid = Integer.parseInt(arguments.get(0));
          int flightNum = Integer.parseInt(arguments.get(1));
          boolean success = RM.deleteFlight(xid, flightNum);
          return success ? "true" : "false";
        }

        case "deletecars": {
          // parsing the commands for the "DeleteCars" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          boolean success = RM.deleteCars(xid, location);
          return success ? "true" : "false";
        }

        case "deleterooms": {
          // parsing the commands for the "DeleteRooms" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          boolean success = RM.deleteRooms(xid, location);
          return success ? "true" : "false";
        }

        case "deletecustomer": {
          // parsing the commands for the "DeleteCustomer" command
          int xid = Integer.parseInt(arguments.get(0));
          int customerID = Integer.parseInt(arguments.get(1));
          boolean success = RM.deleteCustomer(xid, customerID);
          return success ? "true" : "false";
        }

        case "queryflight": {
          // parsing the commands for the "QueryFlight" command
          int xid = Integer.parseInt(arguments.get(0));
          int flightNum = Integer.parseInt(arguments.get(1));
          int number = RM.queryFlight(xid, flightNum);
          return Integer.toString(number);
        }

        case "querycars": {
          // parsing the commands for the "QueryCars" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          int number = RM.queryCars(xid, location);
          return Integer.toString(number);
        }

        case "queryrooms": {
          // parsing the commands for the "QueryRooms" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          int number = RM.queryRooms(xid, location);
          return Integer.toString(number);
        }

        case "querycustomer": {
          // parsing the commands for the "QueryCustomer" command
          int xid = Integer.parseInt(arguments.get(0));
          int customerID = Integer.parseInt(arguments.get(1));
          String info = RM.queryCustomerInfo(xid, customerID);
          return info;
        }

        case "queryflightprice": {
          // parsing the commands for the "QueryFlightPrice" command
          int xid = Integer.parseInt(arguments.get(0));
          int flightNum = Integer.parseInt(arguments.get(1));
          int price = RM.queryFlightPrice(xid, flightNum);
          return Integer.toString(price);
        }

        case "querycarsprice": {
          // parsing the commands for the "QueryCarsPrice" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          int price = RM.queryCarsPrice(xid, location);
          return Integer.toString(price);
        }

        case "queryroomsprice": {
          // parsing the commands for the "QueryRoomsPrice" command
          int xid = Integer.parseInt(arguments.get(0));
          String location = arguments.get(1);
          int price = RM.queryRoomsPrice(xid, location);
          return Integer.toString(price);
        }

        case "reserveflight": {
          // parsing the commands for the "ReserveFlight" command
          int xid = Integer.parseInt(arguments.get(0));
          int customerID = Integer.parseInt(arguments.get(1));
          int flightNum = Integer.parseInt(arguments.get(2));
          boolean success = RM.reserveFlight(xid, customerID, flightNum);
          return success ? "true" : "false";
        }

        case "reservecar": {
          // parsing the commands for the "ReserveCar" command
          int xid = Integer.parseInt(arguments.get(0));
          int customerID = Integer.parseInt(arguments.get(1));
          String location = arguments.get(2);
          boolean success = RM.reserveCar(xid, customerID, location);
          return success ? "true" : "false";
        }

        case "reserveroom": {
          // parsing the commands for the "ReserveRoom" command
          int xid = Integer.parseInt(arguments.get(0));
          int customerID = Integer.parseInt(arguments.get(1));
          String location = arguments.get(2);
          boolean success = RM.reserveRoom(xid, customerID, location);
          return success ? "true" : "false";
        }
      }
    } catch (Exception e) {
      System.out.println("Error during execution of the command");
    }
    throw new IllegalArgumentException("Illigal command.");
  }

}

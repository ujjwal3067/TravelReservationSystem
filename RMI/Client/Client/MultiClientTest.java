package Client;

import java.util.*;
import java.io.*;

public class MultiClientTest {

  static List<Thread> threadPool = new ArrayList<>();

  public static void main(String[] args) {
    Map<Integer, Long> output = new HashMap<>();
    int numberOfClients = Integer.parseInt(args[0]);
    int numberOfTransactions = Integer.parseInt(args[1]);

    for (int i = 1; i <= numberOfTransactions; i++) {
      ClientUnitTest.newTest("Number of transactions: " + i);
      testMultiThread(i, numberOfClients);

      Thread mainThread = new Thread() {
        public void run() {
          try {
            for (int i = 0; i < threadPool.size(); i++) {
              long interval = 1000 / threadPool.size();
              threadPool.get(i).join();
              sleep(interval);
              threadPool.get(i).start();
            }
          } catch (Exception e) {
            System.out.println("Error in main thread!!!!!!!!");
            e.printStackTrace();
          }
        }
      };

      try {
        long startTime = System.nanoTime();
        // sleep((long) 40);
        mainThread.start();
        // wait until the mainThread finishes
        mainThread.join();
        // sleep(40);
        long elapsedTime = (System.nanoTime() - startTime) / 1000;
        System.out.println(
            "\n \n--------->>>> Response time is: " + elapsedTime + " x 10^3 nanoseconds <<<<---------- \n \n");

        output.put(i, elapsedTime);
      } catch (Exception e) {
        System.out.println("ERROR IN MAIN THREAD !!!!!!!!!!");
        e.printStackTrace();
      }
    }

    outputFile(output);
  }

  public static void outputFile(Map<Integer, Long> map) {
    try {
      FileWriter myWriter = new FileWriter("output.txt");
      System.out.println("\n\n\n\n---------->Start writing to file: <----------\n\n\n\n");
      for (Integer key : map.keySet()) {
        myWriter.write(Integer.toString(key) + "," + Long.toString(map.get(key)) + "\n");
      }
      // output:
      // numberOfTransactions, totalResponseTime
      // myWriter.write(map.size() + "," + Long.toString(calculateTotalTime(map)) +
      // "\n");
      myWriter.close();
      System.out.println("-------->Successfully wrote to the file.<----------\n\n\n\n");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public static long calculateTotalTime(Map<Integer, Long> map) {
    long totalTime = 0;
    for (long responseTime : map.values()) {
      totalTime += responseTime;
    }
    return totalTime;
  }

  public static void testMultiThread(int numberOfTransactions, int numberOfClients) {
    ClientTestCommand commandBuilder = new ClientTestCommand();
    assert numberOfTransactions > 0;
    List<RMIClient> clientList = new ArrayList<>();

    RMIClient testClient = new RMIClient("lab2-21", "middleware");

    for (int i = 0; i < numberOfClients; i++) {
      clientList.add(new RMIClient("lab2-21", "middleware"));
    }

    for (int i = 0; i < numberOfTransactions; i++) {
      // try {
      Thread thread = new Thread() {
        public void run() {
          int numberOfItems = 10;
          int pricePerItem = 20;
          int itemId = (int) (Math.random() * 10 + 1);

          try {
            Vector<String> testStartArgs = new Vector<>();
            testStartArgs.add("start");
            String xid = testClient.execute(Command.Start, testStartArgs);

            // multiple clients access the same transaction
            for (int j = 0; j < clientList.size(); j++) {
              // T1 WRITE on Flight
              clientList.get(j).execute(Command.AddFlight,
                  commandBuilder.writeFlight(xid, itemId, numberOfItems, pricePerItem));

              // T1 READ on Flight
              clientList.get(j).execute(Command.QueryFlight, commandBuilder.readFlight(xid, itemId));

              // T1 WRITE on Room
              clientList.get(j).execute(Command.AddRooms,
                  commandBuilder.writeRoom(xid, itemId, numberOfItems, pricePerItem));

              // T1 READ on Room
              clientList.get(j).execute(Command.QueryRooms, commandBuilder.readRooms(xid, itemId));

              // T1 WRITE on Car
              clientList.get(j).execute(Command.AddCars,
                  commandBuilder.writeCar(xid, itemId, numberOfItems, pricePerItem));

              // T1 READ on Car
              clientList.get(j).execute(Command.QueryCars, commandBuilder.readCars(xid, itemId));

              Vector<String> commitCommand = new Vector<>();
              commitCommand.add("commit");
              commitCommand.add(xid);
              testClient.execute(Command.Commit, commitCommand);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };

      threadPool.add(thread);

      // } catch (Exception e) {
      // e.printStackTrace();
      // }
    }

  }

}

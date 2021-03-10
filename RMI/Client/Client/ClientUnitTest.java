package Client;

import Server.Interface.TransactionAbortedException;

public class ClientUnitTest {
    private static ClientTestCommand commandBuilder = new ClientTestCommand();
    private static String host = "lab2-21";
    private static String server = "middleware";

    public static void main(String[] args) {
        testDeadlock();
        testLockConversion();
        testWaitLock();
    }

    public static void testDeadlock() {
        newTest("Deadlock Test");
        RMIClient client1 = new RMIClient(host, server);
        RMIClient client2 = new RMIClient(host, server);

        try {
            String xid1 = client1.execute(Command.Start, commandBuilder.start());
            String xid2 = client2.execute(Command.Start, commandBuilder.start());

            // Add new cars and flights.
            client1.execute(Command.AddCars, commandBuilder.writeCar(xid1, 1024, 10, 100));
            client2.execute(Command.AddFlight, commandBuilder.writeFlight(xid2, 2048, 10, 100));

            // Read new flights and cars. (Deadlock)
            client1.execute(Command.QueryFlight, commandBuilder.readFlight(xid1, 2048));
            client2.execute(Command.QueryCars, commandBuilder.readFlight(xid2, 1024));

            client1.execute(Command.Commit, commandBuilder.commit(xid1));
            client2.execute(Command.Commit, commandBuilder.commit(xid2));

            fail("Deadlock expected");
        } catch (TransactionAbortedException e) {
            pass("Deadlock pass.");
        } catch (Exception e) {
            fail("Deadlock expected");
        }
    }

    public static void testLockConversion() {
        newTest("Lock Conversion Test");
        RMIClient client1 = new RMIClient(host, server);
        RMIClient client2 = new RMIClient(host, server);

        try {
            int numOfSeats = 100;
            int flightNum = (int) (Math.random() * 1024) + 200;

            String xid1 = client1.execute(Command.Start, commandBuilder.start());
            String xid2 = client2.execute(Command.Start, commandBuilder.start());

            // Client1 Add new flight
            client1.execute(Command.AddFlight, commandBuilder.writeFlight(xid1, flightNum, numOfSeats, 100));
            client1.execute(Command.Commit, commandBuilder.commit(xid1));

            // Client2 Read the new flight
            String seats = client2.execute(Command.QueryFlight, commandBuilder.readFlight(xid2, flightNum));
            assertEqual(Integer.toString(numOfSeats), seats);

            // Client2 Write the new flight (Lock conveersion)
            client2.execute(Command.AddFlight, commandBuilder.writeFlight(xid2, flightNum, numOfSeats, 100));
            seats = client2.execute(Command.QueryFlight, commandBuilder.readFlight(xid2, flightNum));
            assertEqual(Integer.toString(numOfSeats * 2), seats);

            client2.execute(Command.Commit, commandBuilder.commit(xid2));

            pass("Lock Conversion Passed.");

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public static void testWaitLock() {
        newTest("Wait Lock Test");
        RMIClient client1 = new RMIClient(host, server);
        RMIClient client2 = new RMIClient(host, server);

        try {
            int rooms = 777;
            int roomId = (int) (Math.random() * 1024) + 200;

            String xid1 = client1.execute(Command.Start, commandBuilder.start());
            String xid2 = client2.execute(Command.Start, commandBuilder.start());

            // Client1 Add new flights
            client1.execute(Command.AddRooms, commandBuilder.writeRoom(xid1, roomId, rooms, 100));
            new Thread() {
                public void run() {
                    try {
                        // Client2 Read new flights
                        String numRooms = client2.execute(Command.QueryRooms, commandBuilder.readRooms(xid2, roomId));
                        assertEqual(Integer.toString(rooms * 2), numRooms);
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }

                }
            }.start();
            client1.execute(Command.AddRooms, commandBuilder.writeRoom(xid1, roomId, rooms, 100));
            client1.execute(Command.Commit, commandBuilder.commit(xid1));

            Thread.sleep(100);
            client2.execute(Command.Commit, commandBuilder.commit(xid2));

            pass("Wait lock Passed.");

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public static void fail(String msg) {
        System.out.println("---------- TEST FAILED!!! ----------");
        System.out.println("---------- " + msg + " ----------");
    }

    public static void fail() {
        System.out.println("---------- TEST FAILED!!! ----------");
    }

    public static void pass(String msg) {
        System.out.println("---------- TEST PASSED!!! ----------");
        System.out.println("---------- " + msg + " ----------");
    }

    public static void newTest(String msg) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("---------- " + msg + " ----------");
        System.out.println();
        System.out.println();
    }

    public static void assertEqual(String expected, String actual) throws Exception {
        if (!expected.equals(actual)) {
            fail("Not Equal: " + expected + ", " + actual);
            throw new Exception("Not Equal: " + expected + ", " + actual);
        }
    }
}

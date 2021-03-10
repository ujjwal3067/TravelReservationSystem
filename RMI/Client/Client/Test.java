import java.util.*;
import java.io.*;

public class Test {

  public static void main(String[] args) {
    Thread mainThread = new Thread() {
      public void run() {
        try {
          for (int i = 0; i < 3; i++) {
            sleep(2);
          }
        } catch (Exception e) {
          System.out.println("Error in main thread!!!!!!!!");
          e.printStackTrace();
        }
      }
    };
    try {
      long startTime = System.nanoTime();
      mainThread.start();
      // wait until the mainThread finishes
      mainThread.join();
      long elapsedTime = (System.nanoTime() - startTime) / 1000;
      System.out
          .println("\n \n--------->>>> Response time is: " + elapsedTime + " x 10^3 nanoseconds <<<<---------- \n \n");
    } catch (Exception e) {
      e.printStackTrace();
    }

  };

}

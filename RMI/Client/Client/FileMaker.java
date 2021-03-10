import java.io.*;
import java.util.*;

public class FileMaker {
    List<Float> transactionTime = new ArrayList<>();
    List<Float> responseTime = new ArrayList<>();

    public FileMaker(ArrayList<Float> transactionTime, ArrayList<Float> responseTime) {
        this.transactionTime = transactionTime;
        this.responseTime = responseTime;
    }

    public void Makefile() {
        try {
            FileWriter writer = new FileWriter("output.txt");
            for (int i = 0; i < transactionTime.size(); i++) {
                StringBuilder str = new StringBuilder();
                str.append(transactionTime.get(i).toString());
                str.append(transactionTime.get(i).toString());
                writer.write(str.toString());
                writer.close();
            }

        } catch (IOException e) {
            System.out.println("ERROR: while creating output file");
            e.printStackTrace();
        }
    }
}

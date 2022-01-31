package projet.Etude1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvWriter {
  public StringBuilder sb;
  public String path;

  public CsvWriter(String path) {

    sb = new StringBuilder();
    this.path = path;

  }

  public void write(long time, int round, long nbMessages) {

    try (
        PrintWriter writer = new PrintWriter(new FileWriter(path, true))) {

      sb.append(time);
      sb.append(',');
      sb.append(round);
      sb.append(',');
      sb.append(nbMessages);
      sb.append('\n');

      writer.write(sb.toString());

      System.out.println("done!");

    } catch (

    IOException e) {
      e.printStackTrace();
    }

  }
}
package projet.Etude2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvWriter2 {
  public StringBuilder sb;
  public String path;

  public CsvWriter2(String path) {

    sb = new StringBuilder();
    this.path = path;

  }

  public void write(long time, long nbMessages) {

    try (
        PrintWriter writer = new PrintWriter(new FileWriter(path, true))) {

      sb.append(time);
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
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Sim {

  private static Map<Train, TrainThread> train_threads = new HashMap<>();
  private static Map<Passenger, PassengerThread> passenger_threads = new HashMap<>();


  public static void run_sim(MBTA mbta, Log log) {
    for (Train t: mbta.trains.keySet()) {
      train_threads.put(t, new TrainThread(mbta, t, log));
    }
    for (Passenger p: mbta.passengers.keySet()) {
      passenger_threads.put(p, new PassengerThread(mbta, p, log));
    }
    for (PassengerThread pt: passenger_threads.values()) {
      pt.start();
    }
    for (TrainThread tt: train_threads.values()) {
      tt.start();
    }
    for (TrainThread tt: train_threads.values()) {
      try {
        tt.join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (PassengerThread pt: passenger_threads.values()) {
      try {
        pt.join();
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("usage: ./sim <config file>");
      System.exit(1);
    }

    MBTA mbta = new MBTA();
    mbta.loadConfig(args[0]);

    Log log = new Log();

    run_sim(mbta, log);

    String s = new LogJson(log).toJson();
    PrintWriter out = new PrintWriter("log.json");
    out.print(s);
    out.close();

    mbta.reset();
    mbta.loadConfig(args[0]);
    Verify.verify(mbta, log);
  }
}

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PSTTest {
    public static void station_test() {
        Station s1 = Station.make("Tufts");
        Station s2 = Station.make("Davis");

        assert s1.toString().equals("Tufts");
        assert s2.toString().equals("Davis");

        Station s1_copy = Station.make("Tufts");
        assert Objects.equals(s1, s1_copy);
    }
    public static void passenger_test() {
        Passenger p1 = Passenger.make("Ranvir");
        Passenger p2 = Passenger.make("Malik");

        assert p1.toString().equals("Ranvir");
        assert p2.toString().equals("Malik");

        Passenger p1_copy = Passenger.make("Ranvir");
        assert Objects.equals(p1, p1_copy);
    }
    public static void train_test() {
       Train t1 = Train.make("green");
       Train t2 = Train.make("red");

        assert t1.toString().equals("green");
        assert t2.toString().equals("red");

       Train t1_copy = Train.make("green");
        assert Objects.equals(t1, t1_copy);
    }
    public static void config_test(String filename) {
        MBTA sim = new MBTA();
        sim.loadConfig(filename);
    }
    public static void verifier_test(String filename) {
        MBTA sim = new MBTA();
        sim.loadConfig(filename);
        Log log = new Log();
        log.passenger_boards(Passenger.make("Bob"), Train.make("green"), Station.make("Tufts"));
        log.train_moves(Train.make("green"), Station.make("Tufts"), Station.make("East Sommerville"));
        log.train_moves(Train.make("green"), Station.make("East Sommerville"), Station.make("Lechmere"));
        log.train_moves(Train.make("green"), Station.make("Lechmere"), Station.make("North Station"));
        log.train_moves(Train.make("green"), Station.make("North Station"), Station.make("Government Center"));
        log.train_moves(Train.make("green"), Station.make("Government Center"), Station.make("Park"));
        log.passenger_deboards(Passenger.make("Bob"), Train.make("green"), Station.make("Park"));

        Verify.verify(sim, log);
    }
    public static void main(String[] args) {
        station_test();
        passenger_test();
        train_test();
        config_test(args[0]);
        verifier_test(args[0]);
    }
}

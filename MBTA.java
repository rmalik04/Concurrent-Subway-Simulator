import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MBTA {

  public Map<String, List<Station>> lines = new HashMap<>();
  public Map<String, List<Station>> journeys = new HashMap<>();

  public Map<Train, Station> trains = new HashMap<>();

  public Map<Train, List<Passenger>> onboard = new HashMap<>();
  public Map<Passenger, Station> passengers = new HashMap<>();
  public Map<Station, Train> station_train = new HashMap<>();
  public Map<Passenger, Train> pass_train = new HashMap<>();
  // Creates an initially empty simulation
  public MBTA() { }

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
      List<Station> line_stations = new ArrayList<>();
      for (String station_name: stations) {
        Station s = Station.make(station_name);
        line_stations.add(s);
      }
      lines.put(name, line_stations);
      trains.put(Train.make(name), line_stations.get(0));
      onboard.put(Train.make(name), new ArrayList<>());

      for(Station s: line_stations) {
          station_train.put(s, null);
      }
      station_train.replace(line_stations.get(0), Train.make(name));
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
      List<Station> passenger_stations = new ArrayList<>();
      for (String station_name: stations) {
        passenger_stations.add(Station.make(station_name));
      }
      journeys.put(name, passenger_stations);
      passengers.put(Passenger.make(name), passenger_stations.get(0));
      pass_train.put(Passenger.make(name), null);
  }
  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
      for (String line: lines.keySet()) {
          Train t = Train.make(line);
          if (!trains.containsKey(t) || !trains.get(t).equals(lines.get(line).get(0))) {
              throw new RuntimeException();
          }
      }
      for (String name: journeys.keySet()) {
          Passenger p = Passenger.make(name);
          if (!passengers.containsKey(p) || !passengers.get(p).equals(journeys.get(name).get(0))) {
              throw new RuntimeException();
          }
      }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
      if (!journeys.isEmpty() || !passengers.isEmpty()) {
          throw new RuntimeException();
      }
      for (Train t: onboard.keySet()) {
          if (!onboard.get(t).isEmpty()) {
              throw new RuntimeException();
          }
      }
  }

  // reset to an empty simulation
  public void reset() {
    lines.clear();
    journeys.clear();
    trains.clear();
    onboard.clear();
    passengers.clear();
    station_train.clear();
    pass_train.clear();
  }

  // adds simulation configuration from a file
  public void loadConfig(String filename) {
    Gson gson = new Gson();
    try {
      String json_content = new String(Files.readAllBytes(Paths.get(filename)));
      Config c = gson.fromJson(json_content, Config.class);
      for (String line: c.lines.keySet()) {
        addLine(line, c.lines.get(line));
      }
      for (String trip: c.trips.keySet()) {
        addJourney(trip, c.trips.get(trip));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

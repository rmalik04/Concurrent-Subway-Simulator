import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Station extends Entity {

  private static Map<Integer, Station> stations = new HashMap<>();

  // store map in mbta of locks and
//  public ReentrantLock station_lock = new ReentrantLock();
//  public Condition c = station_lock.newCondition();
//  public Train at_station;

  private Station(String name) { super(name); }
  public static Station make(String name) {
    // create new object
    Station s = new Station(name);
    int hash_code = s.hashCode();
    // look up object in cache, return existing object if in cache
    if (stations.containsKey(hash_code)) {
      return stations.get(hash_code);
    }
    else {
      // return newly created object and add to cache
      stations.put(hash_code, s);
      return s;
    }
  }
}

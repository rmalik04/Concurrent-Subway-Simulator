import java.util.HashMap;
import java.util.Map;

public class Passenger extends Entity {
  private static Map<Integer, Passenger> passengers = new HashMap<>();
  private Passenger(String name) { super(name); }

  public static Passenger make(String name) {
    // create new object
    Passenger p = new Passenger(name);
    int hash_code = p.hashCode();
    // look up object in cache, return existing object if in cache
    if (passengers.containsKey(hash_code)) {
      return passengers.get(hash_code);
    }
    else {
      // return newly created object and add to cache
      passengers.put(hash_code, p);
      return p;
    }
  }
}

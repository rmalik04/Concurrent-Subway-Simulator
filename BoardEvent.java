import java.util.*;

public class BoardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public BoardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof BoardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " boards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    if (!mbta.passengers.containsKey(p) || !mbta.lines.get(t.toString()).contains(s) || !mbta.journeys.containsKey(p.toString())
        || !(mbta.journeys.get(p.toString()).get(0) == s) || mbta.onboard.get(t).contains(p) || !mbta.trains.get(t).equals(s) || mbta.passengers.get(p) == null) {
      throw new RuntimeException();
    }
    mbta.journeys.get(p.toString()).remove(s);
    mbta.onboard.get(t).add(p);
    mbta.passengers.replace(p, null);

  }
}

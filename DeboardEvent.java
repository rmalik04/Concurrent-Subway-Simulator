import java.util.*;

public class DeboardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public DeboardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof DeboardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " deboards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    if (!mbta.passengers.containsKey(p) || !mbta.lines.get(t.toString()).contains(s) || !(mbta.journeys.get(p.toString()).get(0) == s)
        || !mbta.onboard.get(t).contains(p) || !mbta.trains.get(t).equals(s) || mbta.passengers.get(p) != null) {
      throw new RuntimeException();
    }
    // remove passenger from train and reset dest
    mbta.onboard.get(t).remove(p);
    mbta.pass_train.replace(p, null);
    // update new station passenger is at
    mbta.passengers.replace(p, s);
    // if the station is the end of passenger's journey, delete the passenger
    if (mbta.journeys.get(p.toString()).get(mbta.journeys.get(p.toString()).size() - 1) == s) {
      mbta.passengers.remove(p);
      mbta.journeys.remove(p.toString());
    }
  }
}

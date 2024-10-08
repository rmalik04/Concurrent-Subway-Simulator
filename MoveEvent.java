import java.util.*;

public class MoveEvent implements Event {
  public final Train t; public final Station s1, s2;
  public MoveEvent(Train t, Station s1, Station s2) {
    this.t = t; this.s1 = s1; this.s2 = s2;
  }
  public boolean equals(Object o) {
    if (o instanceof MoveEvent e) {
      return t.equals(e.t) && s1.equals(e.s1) && s2.equals(e.s2);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(t, s1, s2);
  }
  public String toString() {
    return "Train " + t + " moves from " + s1 + " to " + s2;
  }
  public List<String> toStringList() {
    return List.of(t.toString(), s1.toString(), s2.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    if (!mbta.trains.containsKey(t) || !(mbta.lines.get(t.toString()).contains(s1) && mbta.lines.get(t.toString()).contains(s2))
        || !(mbta.trains.get(t) == s1)) {
      throw new RuntimeException();
    }
    for (Train t_check : mbta.trains.keySet()) {
      if (t_check != t) {
        if (mbta.trains.get(t_check) == s2 || mbta.trains.get(t_check) == s1) {
          throw new RuntimeException();
        }
      }
    }
    // check that stations are consecutive
    int start_index = mbta.lines.get(t.toString()).indexOf(s1);
    if (start_index == 0) {
      if ((mbta.lines.get(t.toString()).get(start_index + 1) != s2)) {
        throw new RuntimeException();
      }
    } else if (start_index == mbta.lines.get(t.toString()).size() - 1) {
      if (mbta.lines.get(t.toString()).get(start_index - 1) != s2) {
        throw new RuntimeException();
      }
    } else if (mbta.lines.get(t.toString()).get(start_index + 1) != s2 && mbta.lines.get(t.toString()).get(start_index - 1) != s2) {
      throw new RuntimeException();
    }
    mbta.trains.replace(t, s2);
    mbta.station_train.replace(s1, null);
    mbta.station_train.replace(s2, t);
  }
}

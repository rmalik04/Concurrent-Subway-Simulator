public class TrainThread extends Thread {
    private static MBTA mbta;

    private static Log log;

    private Train curr_train;
    private int index = 0;
    private boolean reverse = false;

    public TrainThread(MBTA state, Train t, Log l) {
        mbta = state;
        this.curr_train = t;
        log = l;
    }

    public void run() {
        try {
            while (!mbta.journeys.isEmpty() && !mbta.passengers.isEmpty()) {
                sleep(10);
                // get train's current station and next station
                Station curr_station;
                Station next_station;
                check_end();
                // based on direction train is going
                if (reverse) {
                    curr_station = mbta.trains.get(curr_train);
                    next_station = mbta.lines.get(curr_train.toString()).get(index - 1);
                } else {
                    curr_station = mbta.trains.get(curr_train);
                    next_station = mbta.lines.get(curr_train.toString()).get(index + 1);
                }
                synchronized (curr_station) {
                    synchronized (next_station) {
                        while (mbta.station_train.get(next_station) != null) {
                            if (mbta.journeys.isEmpty() && mbta.passengers.isEmpty()) {
                                return;
                            }
                            next_station.wait();
                        }
                        // move train and log the move
                        mbta.trains.replace(curr_train, next_station);
                        // add train to next station
                        mbta.station_train.replace(curr_station, null);
                        mbta.station_train.replace(next_station, curr_train);
                        log.train_moves(curr_train, curr_station, next_station);
                        // increment index according to train's direction
                        if (reverse) {
                            this.index--;
                        } else {
                            this.index++;
                        }
                        next_station.notifyAll();
                    }
                    curr_station.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void check_end() {
        if (index == mbta.lines.get(curr_train.toString()).size() - 1) {
            this.reverse = true;
        } else if (index == 0) {
            reverse = false;
        }
    }
}


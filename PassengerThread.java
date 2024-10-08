public class PassengerThread extends Thread {
    private static MBTA mbta;
    private static Log log;
    private Passenger passenger;


    public PassengerThread(MBTA state, Passenger p, Log l) {
        mbta = state;
        this.passenger = p;
        log = l;
    }

    public void run() {
        while (mbta.passengers.containsKey(passenger) && mbta.journeys.containsKey(passenger.toString())) {
            // Boarding passenger
            if (mbta.passengers.get(passenger) != null) {
                // get passenger's station and check if there is a train at station
                Station curr_station = mbta.passengers.get(passenger);
                Train curr_train = mbta.station_train.get(curr_station);
                try {
                    synchronized (curr_station) {
                        while (!(mbta.trains.get(curr_train) == curr_station) || !(mbta.lines.get(curr_train.toString()).contains(mbta.journeys.get(passenger.toString()).get(1)))) {
                            curr_station.wait();
                            curr_train = mbta.station_train.get(curr_station);
                        }
                        // if correct train at station, board the passenger
                        mbta.onboard.get(curr_train).add(passenger);
                        mbta.pass_train.replace(passenger, curr_train);
                        // remove the station from the passenger's journey
                        mbta.journeys.get(passenger.toString()).remove(curr_station);
                        // change current station passenger is at to NULL
                        mbta.passengers.replace(passenger, null);
                        // log board event
                        log.passenger_boards(passenger, curr_train, curr_station);
                        curr_station.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // passenger is on a train and wants to deboard
            else {
                Station dest_station = mbta.journeys.get(passenger.toString()).get(0);
                Train curr_train = mbta.pass_train.get(passenger);
                try {
                    synchronized (dest_station) {
                        // passenger cannot deboard yet
                        while (!(mbta.trains.get(curr_train) == dest_station) || !mbta.onboard.get(curr_train).contains(passenger)) {
                            dest_station.wait();
                            curr_train = mbta.pass_train.get(passenger);
                        }
                        // remove passenger from onboard the train
                        mbta.onboard.get(curr_train).remove(passenger);
                        mbta.pass_train.replace(passenger, null);
                        // update station passenger is at
                        mbta.passengers.replace(passenger, dest_station);
                        // check if passenger has reached their final destination, if they have remove passenger and journey from sim
                        if (mbta.journeys.get(passenger.toString()).get(mbta.journeys.get(passenger.toString()).size() - 1) == mbta.passengers.get(passenger)) {
                            mbta.passengers.remove(passenger);
                            mbta.journeys.remove(passenger.toString());
                        }
                        // log the deboard event
                        log.passenger_deboards(passenger, curr_train, dest_station);
                        // wake all sleeping threads waiting for this lock
                        dest_station.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

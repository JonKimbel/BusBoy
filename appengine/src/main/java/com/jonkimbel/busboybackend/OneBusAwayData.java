package com.jonkimbel.busboybackend;

/**
 * A POJO that matches the layout of OneBusAway API responses, for use with
 * GSON.
 *
 * <p>See GSON docs: https://github.com/google/gson/blob/master/UserGuide.md
 */
public class OneBusAwayData {
  private int code;
  private long currentTime;
  private Data data;
  private String text;
  private int version;

  /** No-args constructor required by GSON. */
  public OneBusAwayData() { }

  public static class Data {
    private Entry entry;
    private References references;

    /** No-args constructor required by GSON. */
    public Data() { }

    public static class Entry {
      private ArrivalsAndDepartures[] arrivalsAndDepartures;
      private String[] nearbyStopIds;
      // private ?[] situationIds;
      private String stopId;

      /** No-args constructor required by GSON. */
      public Entry() { }

      public static class ArrivalsAndDepartures {
        private boolean arrivalEnabled;
        private int blockTripSequence;
        private boolean departureEnabled;
        private float distanceFromStop;
        // private ? frequency;
        private long lastUpdateTime;
        private int numberOfStopsAway;
        private boolean predicted;
        // private ? predictedArrivalInterval;
        private long predictedArrivalTime;
        // private ? predictedDepartureInterval;
        private long predictedDepartureTime;
        private String routeId;
        private String routeLongName;
        private String routeShortName;
        // private ? scheduledArrivalInterval;
        private long scheduledArrivalTime;
        // private ? scheduledDepartureInterval;
        private long scheduledDepartureTime;
        private long serviceDate;
        // private ?[] situationIds;
        private String status;
        private String stopId;
        private int stopSequence;
        private int totalStopsInTrip;
        private String tripHeadsign;
        private String tripId;
        private TripStatus tripStatus;
        private String vehicleId;

        /** No-args constructor required by GSON. */
        public ArrivalsAndDepartures() { }

        public static class TripStatus {


          /**
          "activeTripId": "1_40576125",
          "blockTripSequence": 11,
          "closestStop": "1_18455",
          "closestStopTimeOffset": 2,
          "distanceAlongTrip": 3172.577612709749,
          "frequency": null,
          "lastKnownDistanceAlongTrip": 0,
          "lastKnownLocation": {
            "lat": 47.62242126464844,
            "lon": -122.34236907958984
          },
          "lastKnownOrientation": 0,
          "lastLocationUpdateTime": 1542949057000,
          "lastUpdateTime": 1542949057000,
          "nextStop": "1_18455",
          "nextStopTimeOffset": 2,
          "orientation": 90,
          "phase": "in_progress",
          "position": {
            "lat": 47.62232822013964,
            "lon": -122.342369
          },
          "predicted": true,
          "scheduleDeviation": 118,
          "scheduledDistanceAlongTrip": 3172.577612709749,
          "serviceDate": 1542873600000,
          "situationIds": [],
          "status": "SCHEDULED",
          "totalDistanceAlongTrip": 17053.249925500375,
          "vehicleId": "1_8246"
          */

          /** No-args constructor required by GSON. */
          public TripStatus() { }
        }
      }
    }

    public static class References {

      /** No-args constructor required by GSON. */
      public References() { }
    }
  }
}

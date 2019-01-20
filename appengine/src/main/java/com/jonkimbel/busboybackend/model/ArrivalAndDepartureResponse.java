package com.jonkimbel.busboybackend.model;

import org.onebusaway.api.model.transit.ArrivalAndDepartureV2Bean;

/**
 * A POJO that matches the layout of OneBusAway API responses, for use with
 * GSON.
 *
 * <p>We need to use this class instead of using
 * {@link org.onebusaway.api.model.ResponseBean} directly because GSON cannot
 * deserialize into the generic {@code Object data} in ResponseBean.
 *
 * <p>See GSON docs: https://github.com/google/gson/blob/master/UserGuide.md
 */
public class ArrivalAndDepartureResponse {
  private int code;
  // Skipped field: long currentTime;
  private Data data;
  // Skipped field: String text;
  // Skipped field: int version;

  public int getStatusCode() {
    return code;
  }

  public ArrivalAndDepartureV2Bean[] getArrivals() {
    return data.entry.arrivalsAndDepartures;
  }

  public static class Data {
    Entry entry;
    // Skipped field: Object references;

    public static class Entry {
      ArrivalAndDepartureV2Bean[] arrivalsAndDepartures;
      // Skipped field: Object[] nearbyStopIds;
      // Skipped field: Object[] situationIds;
      // Skipped field: Object[] stopId;
    }
  }
}

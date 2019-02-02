package com.jonkimbel.busboybackend.model;

import javax.annotation.Nullable; // TODO: Needs entry in POM.
import org.onebusaway.api.model.transit.ArrivalAndDepartureV2Bean;

/**
 * A Java object that matches the layout of OneBusAway API responses, for use
 * with GSON.
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
  @Nullable private Data data;
  // Skipped field: String text;
  // Skipped field: int version;

  public int getStatusCode() {
    return code;
  }

  public ArrivalAndDepartureV2Bean[] getArrivals() {
    if (data == null || data.entry == null ||
        data.entry.arrivalsAndDepartures == null) {
      return new ArrivalAndDepartureV2Bean[0];
    }
    return data.entry.arrivalsAndDepartures;
  }

  public static class Data {
    @Nullable Entry entry;
    // Skipped field: Object references;

    public static class Entry {
      @Nullable ArrivalAndDepartureV2Bean[] arrivalsAndDepartures;
      // Skipped field: Object[] nearbyStopIds;
      // Skipped field: Object[] situationIds;
      // Skipped field: Object[] stopId;
    }
  }
}

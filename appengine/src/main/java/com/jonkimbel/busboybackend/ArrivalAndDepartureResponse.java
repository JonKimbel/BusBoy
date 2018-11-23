package com.jonkimbel.busboybackend;

import org.onebusaway.api.model.transit.ArrivalAndDepartureV2Bean;

/**
 * A POJO that matches the layout of OneBusAway API responses, for use with
 * GSON.
 *
 * <p>We need to do this instead of using
 * {@link org.onebusaway.api.model.ResponseBean} directly because GSON cannot
 * deserialize into the generic {@code Object data} in ResponseBean.
 *
 * <p>See GSON docs: https://github.com/google/gson/blob/master/UserGuide.md
 */
public class ArrivalAndDepartureResponse {
  private int code;
  private long currentTime;
  private ArrivalAndDepartureV2Bean data;
  private String text;
  private int version;

  /** No-args constructor required by GSON. */
  public OneBusAwayData() { }

  public int getCode() {
    return code;
  }

  public long getCurrentTime() {
    return currentTime;
  }

  public ArrivalAndDepartureV2Bean getData() {
    return data;
  }

  public String getText() {
    return text;
  }

  public int getVersion() {
    return version;
  }

}

package com.jonkimbel.busboybackend.time;

import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

/**
 * Small util class for server time functionality. Allows time to be mocked out
 * for tests.
 */
public class TimeUtils {
  /** Returns true if California is currently in Daylight Savings Time. */
  public boolean isCaliInDaylightTime() {
    return TimeZone.getTimeZone("America/Los_Angeles")
        .inDaylightTime(new Date());
  }

  /**
  * Returns the current time in milliseconds since the epoch, not adjusted for
  * timezone.
  */
  public long msSinceEpoch() {
    return Instant.now().toEpochMilli();
  }
}

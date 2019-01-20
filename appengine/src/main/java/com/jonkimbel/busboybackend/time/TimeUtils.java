package com.jonkimbel.busboybackend.time;

import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

/**
 * Small util class for server time functionality. Allows time to be mocked out
 * for tests.
 */
public class TimeUtils {
  public boolean isCaInDaylightTime() {
    return TimeZone.getTimeZone("America/Los_Angeles").inDaylightTime(new Date());
  }

  public long msSinceEpoch() {
    return Instant.now().toEpochMilli();
  }
}

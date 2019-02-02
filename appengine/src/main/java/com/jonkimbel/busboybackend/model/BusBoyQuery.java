package com.jonkimbel.busboybackend.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable; // TODO: Needs entry in POM.

/** Model object to represent a query to BusBoyBackend. */
public class BusBoyQuery {
  // Map of querystring keys to values. Contains all key-value pairs in the
  // query string, even if no getter exists for it in the class.
  @VisibleForTesting final Map<String, String> queryMap = new HashMap<>();

  public BusBoyQuery(@Nullable String queryString) {
    parseQueryString(queryMap, queryString);
  }

  @Nullable
  public String getStopId() {
    return queryMap.get("stop");
  }

  public boolean isIncomplete() {
    return !queryMap.containsKey("stop");
  }

  /**
   * Parses a URL query string into the given map. All keys will be lower case.
   *
   * <p>Expected format: [\?]?key=value&key2=value2
   *
   * <p>If an unexpected format is used, a partial map will be returned where
   * possible.
   */
  private static void parseQueryString(Map<String, String> queryMap, @Nullable String queryString) {
    if (queryString == null) {
      return;
    }

    if (queryString.startsWith("?")) {
      queryString = queryString.substring(1);
    }

    String[] clauses = queryString.split("&");
    for (String clause : clauses) {
      int delimiter = clause.indexOf("=");
      String key = delimiter == 0
          ? "" : clause.substring(0, delimiter).toLowerCase();
      if (!queryMap.containsKey(key)) {
        String value = delimiter == clause.length() - 1 ? ""
            : clause.substring(delimiter + 1, clause.length());
        queryMap.put(key, value);
      }
    }
  }
}

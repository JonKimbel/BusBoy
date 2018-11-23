package com.jonkimbel.busboybackend;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable; // Needs entry in POM.

public class HttpUtils {
  /**
   * Parses a URL query string into a map.
   *
   * <p>Expected format: [?]key=value&key2=value2
   *
   * <p>If an unexpected format is used, a partial map will be returned where
   * possible.
   */
  public static Map<String, String> parseQueryString(
      @Nullable String queryString) {
    Map<String, String> map = new HashMap<>();
    if (queryString == null) {
      return map;
    }

    if (queryString.startsWith("?")) {
      queryString = queryString.substring(1);
    }

    String[] clauses = queryString.split("&");
    for (String clause : clauses) {
      int delimiter = clause.indexOf("=");
      String key = delimiter == 0 ? "" : clause.substring(0, delimiter - 1);
      if (!map.containsKey(key)) {
        String value = delimiter == clause.length() - 1 ? ""
            : clause.substring(delimiter + 1, clause.length() - 1);
        map.put(key, value);
      }
    }
  }

  /**
   *
   */
  public static String sendGetRequest(URL url) {
    BufferedReader responseReader =
        new BufferedReader(new InputStreamReader(url.openStream()));
    StringBuffer response = new StringBuffer();

    String line;
    while ((line = responseReader.readLine()) != null) {
      response.append(line);
    }
    responseReader.close();

    return response.toString();
  }
}

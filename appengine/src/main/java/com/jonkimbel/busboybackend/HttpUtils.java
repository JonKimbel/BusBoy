package com.jonkimbel.busboybackend;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class HttpUtils {
  public final static int SC_BAD_REQUEST = 400;
  public final static int SC_INTERNAL_SERVER_ERROR = 500;
  public final static int SC_SERVICE_UNAVAILABLE = 503;

  /**
   * Parses a URL query string into a map. All keys will be lower case.
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
      String key = delimiter == 0
          ? "" : clause.substring(0, delimiter).toLowerCase();
      if (!map.containsKey(key)) {
        String value = delimiter == clause.length() - 1 ? ""
            : clause.substring(delimiter + 1, clause.length());
        map.put(key, value);
      }
    }

    return map;
  }

  public static String sendGetRequest(URL url) throws IOException {
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

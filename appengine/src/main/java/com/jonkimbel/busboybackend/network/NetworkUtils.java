package com.jonkimbel.busboybackend.network;

import com.google.gson.Gson;
import com.jonkimbel.busboybackend.model.ArrivalAndDepartureResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Small util class for networking functionality.
 *
 * <p>In addition to splitting up logic, putting networking stuff here allows
 * responses from other servers to be mocked out for tests.
 */
public class NetworkUtils {
  // HTTP response codes.
  public final static int SC_BAD_REQUEST = 400;
  public final static int SC_INTERNAL_SERVER_ERROR = 500;
  public final static int SC_SERVICE_UNAVAILABLE = 503;

  /**
   * Format string for sending requests to OneBusAway. Takes two arguments:
   *
   * <ol>
   *   <li>A stop ID (string).
   *   <li>A OneBusAway API key (string).
   * </ol>
   */
  private final static String OBA_URL_FORMAT_STRING =
      "http://api.onebusaway.org/api/where/arrivals-and-departures-for-stop" +
      "/%s.json?key=%s";

  private final Gson gson = new Gson();

  /**
   * OneBusAway API key. Contact OBA_API_KEY@soundtransit.org to get a real key
   * once you deploy, but just use "TEST" for development purposes.
   */
  private final String apiKey;

  public NetworkUtils(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Synchronously sends an HTTP GET request to the given URL, and returns the
   * response body as a string.
   */
  private String sendGetRequest(String urlString)
      throws IOException, MalformedURLException {
    URL url = new URL(urlString);
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

  /**
   * Synchronously requests JSON arrival/departure data for the given stop ID.
   *
   * <p>To determine the OBA stop ID for a stop, use
   * http://pugetsound.onebusaway.org/where/text/ to search for stops by their
   * associated route, then get the ID from the URL of the page for a single
   * stop. Stop IDs generally look like {@code N_NNN}.
   */
  public ArrivalAndDepartureResponse getDataForStopId(String stopId)
      throws IOException, MalformedURLException {
    String urlForStopId = String.format(OBA_URL_FORMAT_STRING, stopId, apiKey);
    String json = sendGetRequest(urlForStopId);
    return gson.fromJson(json, ArrivalAndDepartureResponse.class);
  }
}

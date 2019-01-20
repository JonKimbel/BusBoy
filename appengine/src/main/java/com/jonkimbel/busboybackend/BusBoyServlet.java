/*
 * Copyright 2017 Google Inc.
 * Copyright 2018 Jon Kimbel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jonkimbel.busboybackend;

import com.google.gson.Gson;
import com.jonkimbel.busboybackend.model.ArrivalAndDepartureResponse;
import com.jonkimbel.busboybackend.network.NetworkUtils;
import com.jonkimbel.busboybackend.proto.BusBoy;
import com.jonkimbel.busboybackend.time.TimeUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable; // TODO: Needs entry in POM.
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebusaway.api.model.transit.ArrivalAndDepartureV2Bean;

public class BusBoyServlet extends HttpServlet {
  private final static String STOP_QUERY_PARAM = "stop";

  private final static String OBA_URL_FORMAT_STRING =
      "http://api.onebusaway.org/api/where/arrivals-and-departures-for-stop" +
      "/%s.json" +
      // Email OBA_API_KEY@soundtransit.org to get a real key if you're planning
      // on actually using this app beyond testing.
      "?key=TEST";

  private final NetworkUtils networkUtils;
  private final TimeUtils timeUtils;

  @Inject
  public BusBoyServlet(NetworkUtils networkUtils, TimeUtils timeUtils) {
    this.networkUtils = networkUtils;
    this.timeUtils = timeUtils;
  }

  /**
   * Serves HTTP requests to /.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/plain");

    String stopId = getStopIdFromQueryString(request.getQueryString());

    if (stopId == null) {
      response.setStatus(NetworkUtils.SC_BAD_REQUEST);
      response.getWriter().println("Request format: <domain>/?stop=<ID>");
      return;
    }

    ArrivalAndDepartureResponse data;
    try {
      data = getDataForStopId(stopId);
    } catch (MalformedURLException e) {
      response.setStatus(NetworkUtils.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().println("Error creating URL for OneBusAway.");
      return;
    } catch (IOException e) {
      response.setStatus(NetworkUtils.SC_SERVICE_UNAVAILABLE);
      response.getWriter().println("Error sending request to OneBusAway.");
      return;
    }

    BusBoy.Response.Builder protoBuilder = BusBoy.Response.newBuilder()
        .setTime(BusBoy.DisplayedTime.newBuilder()
            .setMsSinceEpoch(timeUtils.msSinceEpoch())
            .setDaylightSavingsTime(timeUtils.isCaInDaylightTime()));

    if (data != null) {
      List<BusBoy.Route> routes = new ArrayList<>();
      for (ArrivalAndDepartureV2Bean arrival : data.getArrivals()) {
        long arrivalTime = arrival.hasPredictedArrivalTime() ?
            arrival.getPredictedArrivalTime() :
            arrival.getScheduledArrivalTime();

        long msToArrival = arrivalTime - timeUtils.msSinceEpoch();

        if (msToArrival >= 0) {
          BusBoy.Route route = BusBoy.Route.newBuilder()
            .setShortName(arrival.getRouteShortName())
            // Headsign is the most expensive part of the response, if something
            // needs to go to save on data this is it.
            .setHeadsign(arrival.getTripHeadsign())
            .build();
          if (!routes.contains(route)) {
            protoBuilder.addRoute(route);
            routes.add(route);
          }

          int msToArrivalInt = (msToArrival > Integer.MAX_VALUE) ?
              Integer.MAX_VALUE : (int) msToArrival;

          protoBuilder.addArrival(BusBoy.Arrival.newBuilder()
              .setRouteIndex(routes.indexOf(route))
              .setMsToArrival(msToArrivalInt)
              .setPredicted(arrival.hasPredictedArrivalTime()));
        }
      }
    }

    protoBuilder.build().writeTo(response.getOutputStream());
  }

  @Nullable
  private String getStopIdFromQueryString(String queryString) {
    Map<String, String> map = networkUtils.parseQueryString(queryString);
    return map.get(STOP_QUERY_PARAM);
  }

  private ArrivalAndDepartureResponse getDataForStopId(String stopId)
      throws MalformedURLException, IOException {
    String urlForStopId = String.format(OBA_URL_FORMAT_STRING, stopId);
    String json = networkUtils.sendGetRequest(urlForStopId);

    Gson gson = new Gson();
    return gson.fromJson(json, ArrivalAndDepartureResponse.class);
  }
}

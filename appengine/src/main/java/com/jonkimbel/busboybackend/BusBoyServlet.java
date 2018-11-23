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

import static javax.servlet.http.SC_BAD_REQUEST;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.annotation.Nullable; // Needs entry in POM.
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BusBoyServlet", value = "/busboy")
public class BusBoyServlet extends HttpServlet {
  private final static String STOP_QUERY_PARAM = "stop";

  private final static String OBA_URL_FORMAT_STRING =
      "http://api.onebusaway.org/api/where/arrivals-and-departures-for-stop" +
      // Email OBA_API_KEY@soundtransit.org to get a real key if you're planning
      // on actually using this app beyond testing.
      "/%s.json?key=TEST";

  /**
   * Serves HTTP requests to /busboy.
   *
   * <p>Request format: /busboy?stop=<ID>
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/plain");

    String stopId = getStopIdFromQueryString(request.getQueryString());

    if (stopId == null) {
      response.setStatus(SC_BAD_REQUEST);
      response.getWriter().println("Request format: /busboy?stop=<ID>");
      return;
    }

    OneBusAwayData data = getDataForStopId(stopId);

    response.getWriter().println("Hello busboy");
  }

  @Nullable
  private static String getStopIdFromQueryString(String queryString) {
    Map<String, String> map = HttpUtils.parseQueryString(queryString);
    return map.get(STOP_QUERY_PARAM);
  }

  private static OneBusAwayData getDataForStopId(String stopId) {
    URL urlForStopId = new URL(String.format(OBA_URL_FORMAT_STRING, stopId));
    String json = HttpUtils.sendGetRequest(urlForStopId);
    Gson gson = new Gson();

    OneBusAwayData obj2 = gson.fromJson(json, OneBusAwayData.class);
  }
}

package com.jonkimbel.busboybackend;

import com.jonkimbel.busboybackend.model.ArrivalAndDepartureResponse;
import com.jonkimbel.busboybackend.model.BusBoyQuery;
import com.jonkimbel.busboybackend.network.NetworkUtils;
import com.jonkimbel.busboybackend.proto.BusBoy;
import com.jonkimbel.busboybackend.time.TimeUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable; // TODO: Needs entry in POM.
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onebusaway.api.model.transit.ArrivalAndDepartureV2Bean;

/**
 * The sole endpoint for the BusBoy backend. Accepts queries of the format
 * {@code <domain>/?stop=<ID>} and responds with an encoded
 * {@link BusBoy.Response}.
 *
 * <p>Wrapped by {@link BusBoyServletWrapper} for dependency injection purposes.
 */
public class BusBoyServlet extends HttpServlet {
  private final NetworkUtils networkUtils;
  private final TimeUtils timeUtils;

  @Inject
  public BusBoyServlet(NetworkUtils networkUtils, TimeUtils timeUtils) {
    this.networkUtils = networkUtils;
    this.timeUtils = timeUtils;
  }

  /** Serves HTTP requests to /. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/plain");

    // Parse request query string.
    BusBoyQuery query = new BusBoyQuery(request.getQueryString());
    if (query.isIncomplete()) {
      response.setStatus(NetworkUtils.SC_BAD_REQUEST);
      response.getWriter().println("Request format: <domain>/?stop=<ID>");
      return;
    }

    // Get data from OneBusAway.
    ArrivalAndDepartureResponse data;
    try {
      data = networkUtils.getDataForStopId(query.getStopId());
    } catch (MalformedURLException e) {
      response.setStatus(NetworkUtils.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().println("Error creating URL for OneBusAway.");
      return;
    } catch (IOException e) {
      response.setStatus(NetworkUtils.SC_SERVICE_UNAVAILABLE);
      response.getWriter().println("Error sending request to OneBusAway.");
      return;
    }

    // Fill response proto and write to HTTP output stream.
    createResponse(data)
        .writeTo(response.getOutputStream());
  }

  /**
   * Returns a filled-out {@link BusBoy.Response} based on the provided data and
   * the current time.
   */
  private BusBoy.Response createResponse(
      ArrivalAndDepartureResponse data) {
    BusBoy.Response.Builder responseBuilder = BusBoy.Response.newBuilder()
        .setTime(BusBoy.DisplayedTime.newBuilder()
            .setMsSinceEpoch(timeUtils.msSinceEpoch())
            .setDaylightSavingsTime(timeUtils.isCaliInDaylightTime()));

    if (data == null) {
      return responseBuilder.build();
    }

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
          // needs to go to save on data this would be it.
          .setHeadsign(arrival.getTripHeadsign())
          .build();
        if (!routes.contains(route)) {
          responseBuilder.addRoute(route);
          routes.add(route);
        }

        int msToArrivalInt = (msToArrival > Integer.MAX_VALUE) ?
            Integer.MAX_VALUE : (int) msToArrival;

        responseBuilder.addArrival(BusBoy.Arrival.newBuilder()
            .setRouteIndex(routes.indexOf(route))
            .setMsToArrival(msToArrivalInt)
            .setPredicted(arrival.hasPredictedArrivalTime()));
      }
    }
    return responseBuilder.build();
  }
}

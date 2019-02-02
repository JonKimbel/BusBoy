package com.jonkimbel.busboybackend;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Thin wrapper around {@link BusBoyServlet} to provide dependency injection.
 */
@WebServlet(name = "BusBoyServletWrapper", value = "/")
public class BusBoyServletWrapper extends HttpServlet {
  private final BusBoyServlet wrappedServlet;

  public BusBoyServletWrapper() {
    ServletComponent component = DaggerServletComponent.create();
    this.wrappedServlet = component.servlet();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    wrappedServlet.doGet(request, response);
  }
}

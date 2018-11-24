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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;
import com.jonkimbel.busboybackend.network.NetworkUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link BusBoyServlet}.
 */
@RunWith(JUnit4.class)
public class BusBoyServletTest {
  private static final String FAKE_URL = "fake.fk/busboy?stop=ID";
  private static final String QUERY_STRING = "stop=ID";
  private static final String FAKE_JSON_RESPONSE = "{ \"code\": 200 }";

  // Set up a helper so that the ApiProxy returns a valid environment for local testing.
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  @Mock private NetworkUtils mockNetworkUtils;
  private StringWriter responseWriter;
  private BusBoyServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    //  Set up some fake HTTP requests
    when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);
    when(mockRequest.getQueryString()).thenReturn(QUERY_STRING);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    servletUnderTest = new BusBoyServlet(mockNetworkUtils);
  }

  @After public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_badQueryString_returnsBadRequest() throws Exception {
    servletUnderTest.doGet(mockRequest, mockResponse);

    verify(mockResponse).setStatus(400);
    assertThat(responseWriter.toString())
        .contains("Request format: <domain>/?stop=<ID>");
  }

  @Test
  public void doGet_failedRequestToOneBusAway_returnsInternalServerError() throws Exception {
    when(mockNetworkUtils.parseQueryString(QUERY_STRING)).thenReturn(ImmutableMap.of("stop", "ID"));
    when(mockNetworkUtils.sendGetRequest(any())).thenThrow(MalformedURLException.class);

    servletUnderTest.doGet(mockRequest, mockResponse);

    verify(mockResponse).setStatus(500);
    assertThat(responseWriter.toString())
        .contains("Error creating URL for OneBusAway.");
  }

  @Test
  public void doGet_failedRequestToOneBusAway_returnsServiceUnreachable() throws Exception {
    when(mockNetworkUtils.parseQueryString(QUERY_STRING)).thenReturn(ImmutableMap.of("stop", "ID"));
    when(mockNetworkUtils.sendGetRequest(any())).thenThrow(IOException.class);

    servletUnderTest.doGet(mockRequest, mockResponse);

    // verify(mockResponse).setStatus(503);
    assertThat(responseWriter.toString())
        .contains("Error sending request to OneBusAway.");
  }

  @Test
  public void doGet() throws Exception {
    when(mockNetworkUtils.parseQueryString(QUERY_STRING)).thenReturn(ImmutableMap.of("stop", "ID"));
    when(mockNetworkUtils.sendGetRequest(any())).thenReturn(FAKE_JSON_RESPONSE);

    servletUnderTest.doGet(mockRequest, mockResponse);

    // verify(mockResponse).setStatus(503);
    assertThat(responseWriter.toString())
        .contains("Hello busboy");
  }
}

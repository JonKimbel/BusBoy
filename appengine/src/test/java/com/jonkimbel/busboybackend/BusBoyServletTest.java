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
import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link BusBoyServlet}.
 */
@RunWith(JUnit4.class)
public class BusBoyServletTest {
  private static final String FAKE_URL = "fake.fk/busboy?stop=ID";
  // Set up a helper so that the ApiProxy returns a valid environment for local testing.
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private BusBoyServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    //  Set up some fake HTTP requests
    when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);
    when(mockRequest.getQueryString()).thenReturn("stop=ID");

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    servletUnderTest = new BusBoyServlet();
  }

  @After public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGetWritesResponse() throws Exception {
    servletUnderTest.doGet(mockRequest, mockResponse);

    // We expect our hello world response.
    assertThat(responseWriter.toString())
        .named("BusBoyServlet response")
        .contains("Hello busboy");
  }
}
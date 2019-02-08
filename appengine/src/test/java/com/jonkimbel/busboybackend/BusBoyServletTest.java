package com.jonkimbel.busboybackend;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;
import static org.mockito.Mockito.*;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import com.jonkimbel.busboybackend.model.ArrivalAndDepartureResponse;
import com.jonkimbel.busboybackend.network.NetworkUtils;
import com.jonkimbel.busboybackend.ApiKeys;
import com.jonkimbel.busboybackend.proto.BusBoy;
import com.jonkimbel.busboybackend.testing.FakeServletOutputStream;
import com.jonkimbel.busboybackend.time.TimeUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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

  // Set up a helper so that the ApiProxy returns a valid environment for local
  // testing.
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper();

  private final Gson gson = new Gson();

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  @Mock private NetworkUtils mockNetworkUtils;
  @Mock private TimeUtils mockTimeUtils;
  private StringWriter responseWriter;
  private FakeServletOutputStream responseStream;
  private BusBoyServlet servletUnderTest;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    //  Set up some fake HTTP requests.
    when(mockRequest.getRequestURI()).thenReturn(FAKE_URL);
    when(mockRequest.getQueryString()).thenReturn(QUERY_STRING);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    responseStream = new FakeServletOutputStream();
    when(mockResponse.getOutputStream()).thenReturn(responseStream);

    // Set up fake time. These values are referenced by the expected responses
    // in the testdata directory.
    when(mockTimeUtils.isCaliInDaylightTime()).thenReturn(false);
    when(mockTimeUtils.msSinceEpoch()).thenReturn(42L);

    servletUnderTest = new BusBoyServlet(mockNetworkUtils, mockTimeUtils);
  }

  @After public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_badQueryString_returnsBadRequest() throws Exception {
    when(mockRequest.getQueryString()).thenReturn("key=value");

    servletUnderTest.doGet(mockRequest, mockResponse);

    verify(mockResponse).setStatus(400);
    assertThat(responseWriter.toString())
        .contains("Request format: <domain>/?stop=<ID>");
  }

  @Test
  public void doGet_failedRequestToOneBusAway_returnsInternalServerError()
      throws Exception {
    when(mockNetworkUtils.getDataForStopId("ID")).thenThrow(
        MalformedURLException.class);

    servletUnderTest.doGet(mockRequest, mockResponse);

    verify(mockResponse).setStatus(500);
    assertThat(responseWriter.toString())
        .contains("Error creating URL for OneBusAway.");
  }

  @Test
  public void doGet_failedRequestToOneBusAway_returnsServiceUnreachable()
      throws Exception {
    when(mockNetworkUtils.getDataForStopId("ID")).thenThrow(
        IOException.class);

    servletUnderTest.doGet(mockRequest, mockResponse);

    verify(mockResponse).setStatus(503);
    assertThat(responseWriter.toString())
        .contains("Error sending request to OneBusAway.");
  }

  @Test
  public void doGet_time() throws Exception {
    testResponse(/* testFileName = */ "time");
  }

  // TODO: write more test cases to differentiate failure types.
  // @Test
  // public void doGet_arrival() throws Exception {
  //   testResponse(
  //       /* testFileName = */ "arrival"
  //       /* ignoringFields = */ 0);
  // }

  @Test
  public void doGet_fullResponse() throws Exception {
    // This test uses real data so we need a real-ish timestamp.
    when(mockTimeUtils.msSinceEpoch()).thenReturn(1542949430000L);

    testResponse(/* testFileName = */ "full-response");
  }

  private void testResponse(String testFileName, Integer... ignoringFields)
      throws Exception {
    when(mockNetworkUtils.getDataForStopId("ID")).thenReturn(
        readJson("test-oba-response/" + testFileName + ".json"));

    servletUnderTest.doGet(mockRequest, mockResponse);

    BusBoy.Response responseData = BusBoy.Response
        .parseFrom(responseStream.toByteArray());
    assertThat(responseData)
        .ignoringFields(Arrays.asList(ignoringFields))
        .isEqualTo(readProto("expected-response/" + testFileName + ".textpb"));
  }

  private ArrivalAndDepartureResponse readJson(String dataFilePath)
      throws Exception {
    String fileString = readFile(dataFilePath);
    return gson.fromJson(fileString, ArrivalAndDepartureResponse.class);
  }

  private BusBoy.Response readProto(String dataFilePath)
      throws Exception {
    String fileString = readFile(dataFilePath);
    BusBoy.Response.Builder protoBuilder = BusBoy.Response.newBuilder();
    TextFormat.merge(fileString, protoBuilder);
    return protoBuilder.build();
  }

  private String readFile(String dataFilePath) throws Exception {
    byte[] encodedContents = Files.readAllBytes(Paths.get(
        "src/test/java/com/jonkimbel/busboybackend/testdata/" + dataFilePath));
    return new String(encodedContents, StandardCharsets.UTF_8);
  }
}

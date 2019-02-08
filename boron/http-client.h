// A wrapper for around TCPClient that makes HTTP requests. Example usage:
//
// HttpClient httpClient;
// ArrayList<uint8_t> responseBuffer;
//
// http_init(&httpClient, "google.com", "/search?q=banana", 80);
// http_connect(&httpClient);
// http_send_request(&httpClient);
// http_get_response(&httpClient, &responseBuffer);
//
// for (int i = 0; i < responseBuffer.length; i++) {
//   print(responseBuffer.data[i]);
// }
//
// http_clear(&httpClient);
// al_clear(&responseBuffer);
// // httpClient and responseBuffer can now be dropped out of scope OR added to
// // again.

#ifndef HTTP_CLIENT_H
#define HTTP_CLIENT_H

#include <Wire.h>
#include "array-list.h"
#include "spark_wiring_string.h"
#include "spark_wiring_tcpclient.h"
#include "spark_wiring_usbserial.h"

typedef struct {
  TCPClient _tcpClient;
  char* domain;
  char* path;
  int port;
} HttpClient;

// A mix of HTTP status codes and failure codes specific to this implementation.
enum Status {
  // HttpClient messed up.
  HTTP_STATUS_CLIENT_ERROR = 0,

  // The server returned an error status not represented in this enum.
  HTTP_STATUS_UNKNOWN = 1,

  HTTP_STATUS_OK = 200,

  // The request was formatted incorrectly. Possibly HttpClient's fault.
  HTTP_STATUS_BAD_REQUEST = 400,

  HTTP_STATUS_INTERNAL_SERVER_ERROR = 500,

  HTTP_STATUS_SERVICE_UNAVAILABLE = 503,
};

// Initialize a HttpClient with the given server information.
void http_init(
    HttpClient* client,
    const char* domain,
    const char* path,
    int port);

// Starts a TCP connection with the server. Required for sending requests.
bool http_connect(HttpClient* client);

// Sends an HTTP request over the TCP connection. http_connect() must be called
// first.
void http_send_request(HttpClient* client);

bool http_response_ready(HttpClient* client);

// Reads the response from the server, strips out the header, and writes the
// body of the response to the given ArrayList. http_connect() and
// http_send_request() must be called first.
// The provided ArrayList must be un-initialized or cleared.
Status http_get_response(HttpClient* client, ArrayList<uint8_t>* body);

// Free the space used by the HttpClient. After this is called, http_init() must
// be called before the HttpClient is used again.
void http_clear(HttpClient* client);

#endif // ARRAY_LIST_H

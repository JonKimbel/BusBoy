#include <Wire.h>
#include "spark_wiring_string.h"
#include "spark_wiring_tcpclient.h"
#include "spark_wiring_usbserial.h"
#include "array-list.h"

typedef struct {
  TCPClient _tcpClient;
  char* domain;
  char* path;
  int port;
} HttpClient;

enum Status {
  HTTP_STATUS_CLIENT_ERROR = 0,
  HTTP_STATUS_UNKNOWN = 1,
  HTTP_STATUS_OK = 200,
  HTTP_STATUS_BAD_REQUEST = 400,
  HTTP_STATUS_INTERNAL_SERVER_ERROR = 500,
  HTTP_STATUS_SERVICE_UNAVAILABLE = 503,
};

void http_init(
    HttpClient* client,
    char* domain,
    char* path,
    int port);

bool http_connect(HttpClient* client);

void http_send_request(HttpClient* client);

bool http_response_ready(HttpClient* client);

// The body ArrayList must be un-initialized or cleared.
Status http_get_response(HttpClient* client, ArrayList* body);

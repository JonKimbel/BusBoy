#include "http-client.h"

Status _process_header(HttpClient* client);
Status _get_status_from_code(const uint8_t* statusCode);
bool _status_code_equals(const uint8_t* actual, const char* expected);

const char *REQUEST_HEADER_FORMAT_STRING = "GET %s HTTP/1.0";
const char *REQUEST_LINE_FORMAT_STRING = "Host: %s";
const char *ENTITY_HEADER = "Content-Length: 0";

void http_init(
    HttpClient* client,
    const char* domain,
    const char* path,
    int port) {
  // Allocate one extra character for the null terminator (\0).
  client->domain = (char *) malloc((1 + strlen(domain)) * sizeof(char));
  client->path = (char *) malloc((1 + strlen(path)) * sizeof(char));
  strcpy(client->domain, domain);
  strcpy(client->path, path);
  client->port = port;
}

bool http_connect(HttpClient* client) {
  return client->_tcpClient.connect(client->domain, client->port);
}

void http_send_request(HttpClient* client) {
  // Create the formatted strings for the HTTP request header.
  // NOTE: the lengths are "-1" because the format placeholders (%s) will be
  // consumed, but we need to add one space for the null-terminator (\0).
  size_t request_header_length =
      strlen(client->path) + strlen(REQUEST_HEADER_FORMAT_STRING) - 1;
  char *request_header = (char *) malloc(request_header_length * sizeof(char));
  snprintf(request_header, request_header_length,
      REQUEST_HEADER_FORMAT_STRING, client->path);

  size_t request_line_length =
      strlen(client->domain) + strlen(REQUEST_LINE_FORMAT_STRING) - 1;
  char *request_line = (char *) malloc(request_line_length * sizeof(char));
  snprintf(request_line, request_line_length,
      REQUEST_LINE_FORMAT_STRING, client->domain);

  // Write the HTTP header to the TCP connection, with an empty body.
  client->_tcpClient.println(request_header);
  client->_tcpClient.println(request_line);
  client->_tcpClient.println(ENTITY_HEADER);
  client->_tcpClient.println();

  free(request_header);
  free(request_line);
}

bool http_response_ready(HttpClient* client) {
  return client->_tcpClient.connected() && client->_tcpClient.available();
}

Status http_get_response(HttpClient* client, ArrayList<uint8_t>* body) {
  // Read HTTP header.
  Status status = _process_header(client);
  if (status != HTTP_STATUS_OK) {
    return status;
  }

  // Read body of response.
  while(client->_tcpClient.connected()) {
    if (client->_tcpClient.available()) {
      body->add(client->_tcpClient.read());
    }
  }
  client->_tcpClient.stop();

  return status;
}

void http_clear(HttpClient* client) {
  free(client->domain);
  free(client->path);
}

enum HeaderMajorSection {
  STATUS_LINE = 0,
  GENERAL_INFO = 1,
};

enum HeaderSection {
  HTTP_VERSION = 0,
  STATUS_CODE = 1,
  REASON_PHRASE = 2,
};

// Based on these docs: https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html
Status _process_header(HttpClient* client) {
  int headerMajorSection = 0;
  int headerSection = 0;
  uint8_t statusCode[] = "NNN";
  int headerEndSequence = 0;

  while(client->_tcpClient.connected()) {
    if (!client->_tcpClient.available()) {
      continue;
    }

    uint8_t c = client->_tcpClient.read();
    // Look for CRLF to delimit sections of the header, and a double-CRLF to
    // delimit the end of the header.
    if (c == '\r' && headerEndSequence % 2 == 0) {
      headerEndSequence++;
    } else if (c == '\n' && headerEndSequence % 2 == 1) {
      headerEndSequence++;
    } else {
      headerEndSequence = 0;
    }

    // Process sections of the header.
    if (headerEndSequence == 2) {
      if (headerMajorSection == STATUS_LINE) {
        headerMajorSection++;
      }
      headerSection++;
    } else if (headerEndSequence >= 4) {
      // The header was processed successfully.
      return _get_status_from_code(statusCode);
    }

    // Process sections of the status line, which are space-delimited.
    if (c == ' ' && headerMajorSection == STATUS_LINE) {
      headerSection++;
      // Read status code.
      if (headerSection == STATUS_CODE) {
        client->_tcpClient.read(statusCode, 3);
      }
    }
  }

  // Unexpectedly disconnected.
  return HTTP_STATUS_CLIENT_ERROR;
}

Status _get_status_from_code(const uint8_t* statusCode) {
  if (_status_code_equals(statusCode, "200")) {
    return HTTP_STATUS_OK;
  } else if (_status_code_equals(statusCode, "400")) {
    return HTTP_STATUS_BAD_REQUEST;
  } else if (_status_code_equals(statusCode, "500")) {
    return HTTP_STATUS_INTERNAL_SERVER_ERROR;
  } else if (_status_code_equals(statusCode, "503")) {
    return HTTP_STATUS_SERVICE_UNAVAILABLE;
  }
  return HTTP_STATUS_UNKNOWN;
}

bool _status_code_equals(const uint8_t* actual, const char* expected) {
  for (int i = 0; i < 3; i++) {
    if ((char)actual[i] != expected[i]) {
      return false;
    }
  }
  return true;
}

#include <pb_decode.h>
#include "array-list.h"
#include "bus-boy.pb.h"
#include "http-client.h"
#include "LiquidCrystal_I2C.h"

// This file should #define BACKEND_DOMAIN. Defining a separate file allows me
// to hide my domain from source control via .gitignore.
#include "backend-info.h"

// Don't auto-connect to the Particle cloud. Speeds up testing.
// TODO: remove before deployment so firmware can be updated in the field.
SYSTEM_MODE(SEMI_AUTOMATIC);

////////////////////////////////////////////////////////////////////////////////
// DECLARATIONS.

bool decode_route(pb_istream_t *stream, const pb_field_t *field, void **arg);
bool decode_short_name(pb_istream_t *stream, const pb_field_t *field, void **arg);

////////////////////////////////////////////////////////////////////////////////
// STRUCTS.

typedef struct  {
    uint8_t * short_name;
    uint8_t * headsign;
} Route;

////////////////////////////////////////////////////////////////////////////////
// VARIABLES.

// Set the LCD address to 0x27 for a 20 char, 4 line display.
LiquidCrystal_I2C lcd(0x27,20,4);

// Set up the HTTP client for communication with the backend, getting data for
// the Northbound N 34th street stop.
HttpClient httpClient(BACKEND_DOMAIN, "/?stop=1_26860", 80);

// Buffer used to hold raw data from the server.
ArrayList<uint8_t> responseBuffer;

// Data parsed out of the response from the server.
busboy_api_DisplayedTime responseTime;
ArrayList<Route*> routes;
ArrayList<busboy_api_Arrival> arrivals;
ArrayList<busboy_api_TemporaryMessage> temporaryMessages;
ArrayList<busboy_api_TemporaryStyle> temporaryStyles;

////////////////////////////////////////////////////////////////////////////////
// CODE.

void setup() {
  lcd.begin();
  lcd.backlight();

  lcd.clear();
  lcd.print("waiting for network");
  Cellular.on();
  Cellular.connect();
  while (!Cellular.ready()) {
    delay(100);
  }

  lcd.clear();
  lcd.print("connecting...");
  if (!httpClient.connect()) {
    lcd.clear();
    lcd.print("connection failed");
  } else {
    lcd.clear();
    lcd.print("connected");
    httpClient.sendRequest();

    Status status = httpClient.getResponse(&responseBuffer);

    if (status != HTTP_STATUS_OK) {
      lcd.clear();
      lcd.print("http error: ");
      lcd.print(status);
    } else {
      busboy_api_Response response = busboy_api_Response_init_default;
      response.route.funcs.decode = &decode_route;  // pb_callback_t route;
      // pb_callback_t arrival;
      // pb_callback_t temporary_message;
          // pb_callback_t message;
      // pb_callback_t temporary_style;

      pb_istream_t stream = pb_istream_from_buffer(
          responseBuffer.data, responseBuffer.length);
      bool status = pb_decode(&stream, busboy_api_Response_fields, &response);

      if (!status) {
        lcd.clear();
        lcd.print("proto error");
      }
      //  else {
      //   lcd.clear();
      //   lcd.print("successful parse");
      //   lcd.print("    ");
      //
      //   if (response.has_time) {
      //     lcd.print("t=");
      //     lcd.print(response.time.daylight_savings_time ? "dst" : "nodst");
      //     lcd.print(",");
      //     // Hack to print uint64_t through the LCD.
      //     long upper = response.time.ms_since_epoch / 10000;
      //     long lower = response.time.ms_since_epoch % 10000;
      //     lcd.print(upper);
      //     lcd.print(lower);
      //   } else {
      //     lcd.print("no time");
      //   }
      // }
    }
  }

  for(;;);
}

void loop() { /* Not implemented. */ }

////////////////////////////////////////////////////////////////////////////////
// DECODE CALLBACKS.

// Methods in this section are based on
// https://github.com/nanopb/nanopb/blob/e21e78c67cbd6566fe9d8368eeaf3298ae22b75d/examples/network_server/client.c


// busboy_api_DisplayedTime time;
//
// busboy_api_Route busboy_api_Route_init_default busboy_api_Route_fields
// ArrayList routes;
//
// busboy_api_Arrival busboy_api_Arrival_init_default busboy_api_Arrival_fields
// ArrayList arrivals;
//
// busboy_api_TemporaryMessage busboy_api_TemporaryMessage_init_default busboy_api_TemporaryMessage_fields
// ArrayList temporaryMessages;
//
// busboy_api_TemporaryStyle busboy_api_TemporaryStyle_init_default busboy_api_TemporaryStyle_fields
// ArrayList temporaryStyles;

// void foo(void *x) {
//     void **xs = x;
//     int *n = xs[0];
//     char *name = xs[1];
//     printf("foo: %d - %s\n", *n, name);
// }
//
// int main() {
//     int n = 42;
//     char *name = "Arthur";
//     void *foo_load[2];
//     foo_load[0] = &n;
//     foo_load[1] = name;
//
//     foo(foo_load);
//     return 0;
// }


// Decode callback for routes in response messages.
// Based on https://github.com/nanopb/nanopb/blob/e21e78c67cbd6566fe9d8368eeaf3298ae22b75d/examples/network_server/client.c
bool decode_route(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  Route route;

  busboy_api_Route route_proto = busboy_api_Route_init_default;
  // pb_callback_t short_name;
  route_proto.short_name.funcs.decode = &decode_short_name;
  route_proto.short_name.arg = &route.short_name;
  // // pb_callback_t headsign;
  // route.headsign.funcs.decode = &decode_headsign;
  // route.headsign.arg = &route;

  if (!pb_decode(stream, busboy_api_Route_fields, &route_proto)) {
    return false;
  }

  routes.add(&route);
  return true;
}

// Decode callback for short names in route messages.
bool decode_short_name(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  uint8_t *short_name = (uint8_t*) *arg;
  int length = stream->bytes_left;
  short_name = (uint8_t*) malloc(length * sizeof(uint8_t));

  if (!pb_read(stream, short_name, stream->bytes_left)) {
    return false;
  }

  // TODO: decode from ASCII.
  lcd.clear();
  for (int i = 0; i < length; i++) {
    lcd.print(short_name[i]);
  }

  return true;
}

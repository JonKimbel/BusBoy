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

// Field-specific nanopb decoders.
bool decode_route(pb_istream_t *stream, const pb_field_t *field, void **arg);
bool decode_arrival(pb_istream_t *stream, const pb_field_t *field, void **arg);
bool decode_temporaryMessage(pb_istream_t *stream, const pb_field_t *field, void **arg);
bool decode_temporaryStyle(pb_istream_t *stream, const pb_field_t *field, void **arg);

// Generic nanopb decoders.
bool decode_string(pb_istream_t *stream, const pb_field_t *field, void **arg);

////////////////////////////////////////////////////////////////////////////////
// STRUCTS.

// We define our own version of any message that contains a pb_callback_t so we
// can store the data after parsing-time. Without these, we couldn't save
// repeated fields or strings for later consumption.

typedef struct  {
  char *short_name;
  char *headsign;
} Route;

typedef struct {
  busboy_api_TimeFrame time_frame;
  char *message;
  busboy_api_ColorScheme color_scheme_override;
} TemporaryMessage;

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
ArrayList<busboy_api_Arrival*> arrivals;
ArrayList<TemporaryMessage*> temporaryMessages;
ArrayList<busboy_api_TemporaryStyle*> temporaryStyles;

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
      response.route.funcs.decode = &decode_route;
      response.arrival.funcs.decode = &decode_arrival;
      response.temporary_message.funcs.decode = &decode_temporaryMessage;
      response.temporary_style.funcs.decode = &decode_temporaryStyle;

      pb_istream_t stream = pb_istream_from_buffer(
          responseBuffer.data, responseBuffer.length);
      bool status = pb_decode(&stream, busboy_api_Response_fields, &response);

      if (!status) {
        lcd.clear();
        lcd.print("proto error");
      } else {
        responseTime = response.time;

        lcd.clear();
        // NOTE: this is using some transitive dependency's "min" declaration,
        // which is pretty jank. Would be better if the app was in its own
        // namespace.
        for (int i = 0; i < min(arrivals.length, 4); i++) {
          busboy_api_Arrival *arrival = arrivals.data[i];
          Route *route = routes.data[arrival->route_index];

          int minutes_to_arrival = (int)(arrival->ms_to_arrival / 60000);

          lcd.setCursor(0, i);
          lcd.printf("%s %s %d",
              route->short_name, route->headsign, minutes_to_arrival);
        }
      }
    }
  }

  for(;;);
}

void loop() { /* Not implemented. */ }

////////////////////////////////////////////////////////////////////////////////
// NANOPB DECODE CALLBACKS.

bool decode_route(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  Route route;

  busboy_api_Route route_proto = busboy_api_Route_init_default;
  // pb_callback_t short_name;
  route_proto.short_name.funcs.decode = &decode_string;
  route_proto.short_name.arg = &route.short_name;
  // pb_callback_t headsign;
  route_proto.headsign.funcs.decode = &decode_string;
  route_proto.headsign.arg = &route.headsign;

  if (!pb_decode(stream, busboy_api_Route_fields, &route_proto)) {
    return false;
  }

  routes.add(&route);
  return true;
}

bool decode_arrival(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  busboy_api_Arrival arrival_proto = busboy_api_Arrival_init_default;
  if (!pb_decode(stream, busboy_api_Arrival_fields, &arrival_proto)) {
    return false;
  }

  arrivals.add(&arrival_proto);
  return true;
}

bool decode_temporaryMessage(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  TemporaryMessage temporaryMessage;

  busboy_api_TemporaryMessage temporaryMessage_proto = busboy_api_TemporaryMessage_init_default;
  // pb_callback_t message;
  temporaryMessage_proto.message.funcs.decode = &decode_string;
  temporaryMessage_proto.message.arg = &temporaryMessage.message;

  if (!pb_decode(stream, busboy_api_TemporaryMessage_fields, &temporaryMessage_proto)) {
    return false;
  }

  temporaryMessage.time_frame = temporaryMessage_proto.time_frame;
  temporaryMessage.color_scheme_override = temporaryMessage_proto.color_scheme_override;

  temporaryMessages.add(&temporaryMessage);
  return true;
}

bool decode_temporaryStyle(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  busboy_api_TemporaryStyle temporaryStyle_proto = busboy_api_TemporaryStyle_init_default;
  if (!pb_decode(stream, busboy_api_TemporaryStyle_fields, &temporaryStyle_proto)) {
    return false;
  }

  temporaryStyles.add(&temporaryStyle_proto);
  return true;
}

// The pb_callback_t.arg should be a non-initialized char*.
bool decode_string(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  uint8_t *buffer = (uint8_t*) malloc(stream->bytes_left * sizeof(uint8_t));
  if (!pb_read(stream, buffer, stream->bytes_left)) {
    return false;
  }

  *arg = (char*) buffer;
  return true;
}

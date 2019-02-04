// #include <Wire.h>
#include <pb_decode.h>
// #include "spark_wiring_string.h"
// #include "spark_wiring_tcpclient.h"
// #include "spark_wiring_usbserial.h"
#include "LiquidCrystal_I2C.h"
#include "bus-boy.pb.h"
#include "array-list.h"
#include "http-client.h"

// Don't auto-connect to the Particle cloud. Speeds up testing.
// TODO: remove before deployment so firmware can be updated in the field.
SYSTEM_MODE(SEMI_AUTOMATIC);

bool decode_route(pb_istream_t *stream, const pb_field_t *field, void **arg);

// Set the LCD address to 0x27 for a 20 char, 4 line display.
LiquidCrystal_I2C lcd(0x27,20,4);
HttpClient httpClient;
ArrayList responseBuffer;

void setup() {
  http_init(
      &httpClient,
      "api.onebusaway.org",
      "/api/where/arrivals-and-departures-for-stop/1_26860.json?key=TEST",
      80);

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
  if (!http_connect(&httpClient)) {
    lcd.clear();
    lcd.print("connection failed");
  } else {
    lcd.clear();
    lcd.print("connected");
    http_send_request(&httpClient);

    Status status = http_get_response(&httpClient, &responseBuffer);

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
      // TODO: print to LCD in various callbacks?
    }
  }

  for(;;);
}

void loop() { /* Not implemented. */ }

// pb_callback_t route;
bool decode_route(pb_istream_t *stream, const pb_field_t *field, void **arg) {
  // Submessages to register callbacks for when implementing:
  // pb_callback_t short_name;
  // pb_callback_t headsign;

  // Example decode:
  // See: https://github.com/nanopb/nanopb/blob/e21e78c67cbd6566fe9d8368eeaf3298ae22b75d/examples/network_server/client.c
  // FileInfo fileinfo = {};
  // if (!pb_decode(stream, FileInfo_fields, &fileinfo)) {
  //   return false;
  // }
  // printf("%-10lld %s\n", (long long)fileinfo.inode, fileinfo.name);

  return false;
}

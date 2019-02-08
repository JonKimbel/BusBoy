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

bool decode_route(pb_istream_t *stream, const pb_field_t *field, void **arg);

// Set the LCD address to 0x27 for a 20 char, 4 line display.
LiquidCrystal_I2C lcd(0x27,20,4);
HttpClient httpClient(BACKEND_DOMAIN, "/?stop=1_26860", 80);
ArrayList<uint8_t> responseBuffer(/* initialLength = */ 20);

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
      // response.route.funcs.decode = &decode_route;  // pb_callback_t route;
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
      } else {
        lcd.clear();
        lcd.print("successful parse");
        lcd.print("    ");

        if (response.has_time) {
          lcd.print("t=");
          lcd.print(response.time.daylight_savings_time ? "dst" : "nodst");
          lcd.print(",");
          // Hack to print uint64_t through the LCD.
          long upper = response.time.ms_since_epoch / 10000;
          long lower = response.time.ms_since_epoch % 10000;
          lcd.print(upper);
          lcd.print(lower);
        } else {
          lcd.print("no time");
        }
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

  return true;
}

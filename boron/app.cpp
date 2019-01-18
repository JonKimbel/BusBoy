#include <Wire.h>
#include <pb_decode.h>
#include "spark_wiring_string.h"
#include "spark_wiring_tcpclient.h"
#include "spark_wiring_usbserial.h"
#include "LiquidCrystal_I2C.h"
#include "bus-boy.pb.h"

// Don't auto-connect to the Particle cloud. Speeds up testing.
// TODO: remove before deployment so firmware can be updated in the field.
SYSTEM_MODE(SEMI_AUTOMATIC);

TCPClient client;
// Set the LCD address to 0x27 for a 20 char, 4 line display.
LiquidCrystal_I2C lcd(0x27,20,4);

bool connect();
void sendRequest();
bool getResponse();

void setup()
{
  //////////////////////////////////////////////////////////////////////////////
  // scratch area - decode proto response from server

  // Todo: get these from the response data.
  uint8_t buffer[128];
  size_t message_length = 64;

  busboy_api_Response response = busboy_api_Response_init_default;
  pb_istream_t stream = pb_istream_from_buffer(buffer, message_length);

  bool status = pb_decode(&stream, busboy_api_Response_fields, &response);
  if (!status) {
    // Todo: handle errors.
  } else {
    // Todo: handle successful response.
  }

  // end scratch area
  //////////////////////////////////////////////////////////////////////////////

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
  if (connect()) {
    lcd.clear();
    lcd.print("connected");
    sendRequest();

    bool somethingReceived = getResponse();

    if (!somethingReceived) {
      lcd.clear();
      lcd.print("nothing received");
    }
  } else {
    lcd.clear();
    lcd.print("connection failed");
  }

  for(;;);
}

void loop() { /* Not implemented. */ }

bool connect() {
  return client.connect("api.onebusaway.org", 80);
}

void sendRequest() {
  client.println("GET /api/where/arrivals-and-departures-for-stop/1_26860.json?key=TEST HTTP/1.0");
  client.println("Host: api.onebusaway.org");
  client.println("Content-Length: 0");
  client.println();
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

bool getResponse() {
  lcd.clear();

  // See HTTP response docs: https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html
  int headerMajorSection = 0;
  int headerSection = 0;
  uint8_t statusCode[] = "NNN";
  int headerEndSequence = 0;

  while(true) {
    if (client.available()) {
      char c = client.read();
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
        break;
      }

      // Process sections of the status line, which are space-delimited.
      if (c == ' ' && headerMajorSection == STATUS_LINE) {
        headerSection++;
        // Read status code.
        if (headerSection == STATUS_CODE) {
          client.read(statusCode, 3);
        }
      }
    }

    if (!client.connected()) {
      lcd.print("Header error: ");
      lcd.print(headerMajorSection);
      lcd.print(" ");
      lcd.print(headerSection);
      return false;
    }
  }

  // Process body.
  int charsPrinted = 0;
  int lcdRow = 0;

  while(true) {
    if (client.available()) {
      char c = client.read();
      if (charsPrinted < 80) {
        if (charsPrinted % 20 == 0) {
          lcd.setCursor(0, lcdRow);
          lcdRow++;
        }
        lcd.print(c);
        charsPrinted++;
      }
    }

    if (!client.connected()) {
      client.stop();
      return charsPrinted > 0;
    }
  }
}

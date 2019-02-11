#include <RGBmatrixPanel.h>

RGBmatrixPanel matrix(
    /* A = */ A0, /* B = */ A1, /* C = */ A2, /* D = */ A3, /* CLK = */ 8,
    /* LAT = */ 10, /* OE = */ 9, /* dbuf = */ false);

void setup() {
  int x, y;
  int hue;
  uint8_t sat, val;
  uint16_t c;

  // matrix.begin();
  // matrix.width()
  // matrix.height()
  // uint16_t color =
  //     matrix.ColorHSV(hue, sat, val, /* gflag (gamma corrected) = */ true);
  // matrix.drawPixel(x, y, color);

}

void loop() {
  // Not implemented.
}

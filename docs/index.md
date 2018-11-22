# Bus Boy

[TOC]

## Directory Structure

*   `docs` contains documentation for the project.
*   `boron` contains code to be run on the Particle Boron (C++).
*   `appengine` contains code to be run on Google App Engine, which will serve
    as the backend to the Boron. See the [Design](#design) section for info on
    why this backend is needed.

## Stop-specific Information

Example API URL:
http://api.onebusaway.org/api/where/arrivals-and-departures-for-stop/1_26860.json?key=TEST

*   Northbound stop ID: 1_26860
*   Southbound stop ID: 1_26510

## The Particle Boron

[Device docs](https://docs.particle.io/reference/device-os/firmware/boron/)

``` bash
particle compile boron --saveTo=out.bin
particle flash --usb out.bin
```

## LCD Wiring

The screen used for testing is a [SunFounder I2C 2004 20x4 LCD](
https://www.amazon.com/gp/product/B01GPUMP9C).

The Boron doesn't have a `5V` rail, and the LCD won't display anything if you try
to power it from the Boron's `3V3` rail, so you'll need to provide 5V to the LCD
separately. I used a laptop power supply and an `L7805`.

| LCD   | Color  | Boron           |
|-------|--------|-----------------|
| `GND` | Black  | Boron `GND`     |
| `VCC` | Orange | External `5V`   |
| `SDA` | Yellow | Boron `D0`      |
| `SCL` | White  | Boron `D1`      |

## Potential JSON Parsers

*   https://github.com/Tencent/rapidjson/blob/master/example/tutorial/tutorial.cpp
*   https://github.com/open-source-parsers/jsoncpp/blob/master/include/json/reader.h
*   https://github.com/nlohmann/json

## Design

*   [HTTP response docs](https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html)

The responses from [the test API URL](
http://api.onebusaway.org/api/where/arrivals-and-departures-for-stop/1_26860.json?key=TEST)
are quite large in comparison to the responses from a custom frontend server:

| Size (bytes) | Format |
|--------------|--------|
| 15838        | `json` |
| 26008        | `xml`  |
| TBD          | `proto`|

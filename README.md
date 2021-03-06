# Bus Boy TL;DR

This directory contains the code and documentation for the Bus Boy, a bus stop
sign powered by the Particle Boron.

See [the main docs](docs/index.md) for more info.

## Setup

Run the following from this directory to set up the pre-commit checks.

```
git config core.hooksPath hooks
```

## Compiling

### API (backend + frontend)

Download the latest stable release of nanopb from
[here](https://jpa.kapsi.fi/nanopb/) and add the contents of generator-bin to
your PATH, then run the following commands from this directory.

```
protoc -I=api --java_out=appengine/src/main/java api/bus-boy.proto
protoc -I=api --nanopb_out=boron api/bus-boy.proto
```

While you're on their site, consider donating to the nanopb developer :)

### AppEngine (backend)

The docs in this section were copied from Google's [appengine-try-java repo](
https://github.com/GoogleCloudPlatform/appengine-try-java).

#### Before you begin

1.  Download and install the [Google Cloud
    SDK](https://cloud.google.com/sdk/docs/).
1.  [Install and configure Apache Maven](http://maven.apache.org/index.html).
1.  [Create a new Google Cloud Platform project, or use an existing one](https://console.cloud.google.com/project).
1.  Initialize the Cloud SDK.

        gcloud init

1.  Install the Cloud SDK `app-engine-java` component.

        gcloud components install app-engine-java

1.  Create a file at
    `appengine/src/main/java/com/jonkimbel/busboybackend/ApiKeys.java` and
    paste in the following:

        package com.jonkimbel.busboybackend;

        public class ApiKeys {
          // OneBusAway API key.
          // Email OBA_API_KEY@soundtransit.org to get a real key if you're
          // planning on actually using this app beyond testing.
          public static final String OBA = "TEST";
        }

#### Running the backend locally

To run the application locally, use the [Maven App Engine
plugin](https://cloud.google.com/appengine/docs/java/tools/using-maven). Run the
following from this directory:

```
mvn clean appengine:run -f appengine
```

View the app at [localhost:8080](http://localhost:8080).

#### Deploying to App Engine

To deploy the app to App Engine, run the following from this directory:

```
mvn clean appengine:deploy -f appengine
```

You **may** need to run `gcloud auth login` before the deploy target will work
correctly.

After the deploy finishes, you can view your application at
`https://YOUR_PROJECT.appspot.com`, where `YOUR_PROJECT` is your Google Cloud
project ID. You can see the new version deployed on the [App Engine section of
the Google Cloud Console](https://console.cloud.google.com/appengine/versions).

### Particle Boron (frontend)

#### Before you begin

1.  Install the Particle CLI following the steps
    [here](https://docs.particle.io/tutorials/developer-tools/cli/).
1.  COMPLETELY OPTIONAL: Set up local build toolchain following
    [these steps](https://docs.particle.io/tutorials/developer-tools/cli/#compile-and-flash-code-locally)
1.  Create a file at `boron/backend-info.h` and `#define BACKEND_DOMAIN` to the
    backend you'd like to communicate with. This will probably be the domain
    Google Cloud tells you after deploy the backend code.

#### Compiling & flashing

To compile the device code, run the following from the boron/ directory:

```
particle compile boron --saveTo=out.bin
```

NOTE: The "boron" in this command refers to the device model, the
[Particle Boron](https://store.particle.io/products/boron-lte). It is NOT
providing the name of the folder the code relies upon.

To flash the compiled binary to a usb-connected device:

1.  Hold down the MODE and RESET buttons.
1.  Release the RESET button. The LED should flash purple.
1.  Once the LED flashes yellow, release the MODE button.
1.  Run this flash command:

        particle flash --usb out.bin

### AVR (frontend screen driver)

This code requires an Arduino UNO or equivalent and one or more of [a specific
line of LED screens sold by Adafruit](https://www.adafruit.com/product/2276).

Adafruit outlines the connection of these panels to an Arduino
[here](https://learn.adafruit.com/32x16-32x32-rgb-led-matrix/).

Install the Arduino IDE from [here](https://www.arduino.cc/en/Main/Software).

Install the [Adafruit GFX](https://github.com/adafruit/Adafruit-GFX-Library) and
[RGB Matrix Panel](https://github.com/adafruit/RGB-matrix-Panel) libraries.

Open `avr/avr.ino` in the Arduino IDE, make sure you have the correct board/port
selected, and use the 'verify' and 'upload' buttons to compile and deploy the
code, respectively.

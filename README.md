# Bus Boy TL;DR

This directory contains the code and documentation for the Bus Boy, a bus stop
sign powered by the Particle Boron.

See [the main docs](docs/index.md) for more info.

## Compiling

### API (backend + frontend)

Install the nanopb version of `protoc` from
[here](https://jpa.kapsi.fi/nanopb/download/) and then run the
following commands from this directory.

```
protoc -I=api --java_out=appengine/src/main/java api/bus-boy.proto
protoc -I=api --nanopb_out=boron/proto api/bus-boy.proto
```

NOTE: nanopb is used instead of Google's proto compiler because it supports
embedded C environments.

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

#### Deploying to App Engine

To run the application locally, use the [Maven App Engine
plugin](https://cloud.google.com/appengine/docs/java/tools/using-maven). Run the
following from this directory:

```
mvn clean appengine:run -f appengine
```

View the app at [localhost:8080](http://localhost:8080).

To deploy the app to App Engine, run the following from this directory:

```
mvn clean appengine:deploy -f appengine
```

After the deploy finishes, you can view your application at
`https://YOUR_PROJECT.appspot.com`, where `YOUR_PROJECT` is your Google Cloud
project ID. You can see the new version deployed on the [App Engine section of
the Google Cloud Console](https://console.cloud.google.com/appengine/versions).

### Particle Boron (frontend)



```
particle compile boron --saveTo=out.bin
particle flash --usb out.bin
```

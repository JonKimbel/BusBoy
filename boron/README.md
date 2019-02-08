This directory contains the client code for the Bus Boy, intended for deployment
on a Particle Boron.

Before this code will compile, you will need to add a file named
`backend-info.h` in this directory containing the following:

```
#define BACKEND_DOMAIN "<your backend's domain name, e.g. google.com>"
```

The .pb files are compiled protobufs. To change them, change the API definition
in the `/api` directory then compile them by following the instructions in the
[main README](/README.md).

See [the main docs](../docs/index.md) for more info.

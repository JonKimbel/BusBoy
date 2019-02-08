This directory contains the server code for the Bus Boy, intended for deployment
to Google Cloud.

Before this code will compile, you will need to add a file named
`ApiKeys.java` in this directory containing the following:

```
package com.jonkimbel.busboybackend;

public class ApiKeys {
  // OneBusAway API key.
  // Email OBA_API_KEY@soundtransit.org to get a real key if you're
  // planning on actually using this app beyond testing.
  public static final String OBA = "TEST";
}
```

See [the main docs](../../../../../../../docs/index.md) for more info.

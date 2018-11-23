package com.jonkimbel.busboybackend;

import dagger.Module;
import dagger.Provides;

@Module
class ServletModule {
  @Provides static HttpUtils provideHttpUtils() {
    return new HttpUtils();
  }
}

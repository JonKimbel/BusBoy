package com.jonkimbel.busboybackend;

import com.jonkimbel.busboybackend.network.NetworkUtils;
import dagger.Module;
import dagger.Provides;

@Module
class ServletModule {
  @Provides static NetworkUtils provideNetworkUtils() {
    return new NetworkUtils();
  }
}

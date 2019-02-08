package com.jonkimbel.busboybackend;

import com.jonkimbel.busboybackend.network.NetworkUtils;
import com.jonkimbel.busboybackend.time.TimeUtils;
import dagger.Module;
import dagger.Provides;

@Module
class ServletModule {
  @Provides static NetworkUtils provideNetworkUtils() {
    return new NetworkUtils(ApiKeys.OBA);
  }

  @Provides static TimeUtils provideTimeUtils() {
    return new TimeUtils();
  }
}

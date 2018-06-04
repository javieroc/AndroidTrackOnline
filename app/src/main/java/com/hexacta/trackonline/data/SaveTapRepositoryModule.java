package com.hexacta.trackonline.data;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * This is used by Dagger to inject the required arguments into the {@link SaveTapRepository}.
 */
@Module public abstract class SaveTapRepositoryModule {

  @Singleton @Provides @Named("Mock") static SaveTapDataSource provideSaveTapFakeDataSource() {
    return new FakeSaveTapData();
  }

}

package com.hexacta.trackonline.data;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton public class SaveTapRepository implements SaveTapDataSource {

  private SaveTapDataSource data;

  @Inject public SaveTapRepository(@Named("Mock") SaveTapDataSource data) {
    this.data = data;
  }

  @Override public Profile getProfile() {
    return data.getProfile();
  }
}

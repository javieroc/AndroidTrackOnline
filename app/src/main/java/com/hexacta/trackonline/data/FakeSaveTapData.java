package com.hexacta.trackonline.data;

import java.util.UUID;
import javax.inject.Inject;

public class FakeSaveTapData implements SaveTapDataSource {

  @Inject public FakeSaveTapData() {
  }

  @Override public Profile getProfile() {
    String id = UUID.randomUUID().toString();
    Profile profile = new Profile(id, id + "_name", id + "_photo", id + "_occupation");
    return profile;
  }
}

package com.hexacta.trackonline.main;

import com.hexacta.trackonline.data.Profile;
import com.hexacta.trackonline.data.SaveTapRepository;
import com.hexacta.trackonline.di.ActivityScoped;

import javax.annotation.Nullable;
import javax.inject.Inject;

@ActivityScoped public class SenderPresenter implements SenderContract.Presenter {

  private final SaveTapRepository mRepository;
  private Profile mProfile;
  private boolean isUrgencyActive;

  @Nullable private SenderContract.View mView;

  @Inject public SenderPresenter(SaveTapRepository repository) {
    this.mRepository = repository;
    this.isUrgencyActive = false;
  }

  @Override public void takeView(SenderContract.View view) {

    mView = view;

    if (mProfile == null) {
      mProfile = mRepository.getProfile();
    }
    if (mView != null) {
      mView.showProfile(mProfile);
    }
  }

  @Override public void dropView() {
    mView = null;
  }

  @Override public void urgency() {
    if (mView != null) {
      if (isUrgencyActive) {
        mView.hideUrgency();
      } else {
        mView.showUrgency();
      }
      isUrgencyActive = !isUrgencyActive;
    }
  }
}

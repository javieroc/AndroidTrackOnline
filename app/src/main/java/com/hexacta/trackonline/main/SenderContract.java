package com.hexacta.trackonline.main;

import com.hexacta.trackonline.base.BasePresenter;
import com.hexacta.trackonline.base.BaseView;
import com.hexacta.trackonline.data.Profile;

public class SenderContract {
  interface View extends BaseView<Presenter> {
    void showProfile(Profile profile);

    void showUrgency();

    void hideUrgency();
  }

  interface Presenter extends BasePresenter<View> {
    void urgency();
  }
}

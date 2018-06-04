package com.hexacta.trackonline.main;

import com.hexacta.trackonline.di.ActivityScoped;
import dagger.Binds;
import dagger.Module;

@Module abstract public class SenderModule {
  @ActivityScoped @Binds abstract SenderContract.Presenter taskPresenter(SenderPresenter presenter);
}

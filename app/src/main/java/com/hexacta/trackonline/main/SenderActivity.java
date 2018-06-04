package com.hexacta.trackonline.main;

import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.transition.TransitionManager;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hexacta.trackonline.R;
import com.hexacta.trackonline.data.Profile;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import javax.inject.Inject;

public class SenderActivity extends DaggerAppCompatActivity implements SenderContract.View {

  @Inject SenderPresenter mPresenter;

  @BindView(R.id.btn_urgency) FloatingActionButton mUrgencyButton;

  @BindView(R.id.btn_emergency) FloatingActionButton mEmergencyButton;

  @BindView(R.id.cl_sender) ConstraintLayout mLayout;


  ConstraintSet mStartConstraints;
  private static final String TAG = SenderActivity.class.getSimpleName();
  private FirebaseDatabase mDatabase;
  private DatabaseReference mRef;

  @Override protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    mPresenter.takeView(this);
    configureConstraints();
  }

  public void configureConstraints() {
    mStartConstraints = new ConstraintSet();
    mStartConstraints.clone(mLayout);
  }

  @Override public void showProfile(Profile profile) {
    Toast.makeText(this, profile.getName(), Toast.LENGTH_SHORT).show();
  }

  @Override public void showUrgency() {
    mEmergencyButton.hide();
    mStartConstraints.clear(R.id.btn_urgency, ConstraintSet.RIGHT);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      TransitionManager.beginDelayedTransition(mLayout);
    }
    mUrgencyButton.setImageResource(R.drawable.ic_alert_off);
    mStartConstraints.applyTo(mLayout);
  }

  @Override public void hideUrgency() {
    mEmergencyButton.show();
    mStartConstraints.connect(R.id.btn_urgency, ConstraintSet.RIGHT, R.id.btn_emergency,
        ConstraintSet.LEFT);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      TransitionManager.beginDelayedTransition(mLayout);
    }
    mUrgencyButton.setImageResource(R.drawable.ic_alert_on);
    mStartConstraints.applyTo(mLayout);
  }

  @OnClick(R.id.btn_urgency) public void onUrgency() {
    mPresenter.urgency();
  }

  @Override protected void onStop() {
    super.onStop();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mPresenter.dropView();
  }
}

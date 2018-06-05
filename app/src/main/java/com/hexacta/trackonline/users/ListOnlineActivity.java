package com.hexacta.trackonline.users;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hexacta.trackonline.R;

public class ListOnlineActivity extends AppCompatActivity {

  private String TAG = "TrackingUserOnline";

  // Firebase
  DatabaseReference onlineRef, currentUserRef, counterRef, locationsRef;
  FirebaseRecyclerAdapter<User, ListOnlineViewHolder> mAdapter;

  // View
  RecyclerView listOnline;
  RecyclerView.LayoutManager layoutManager;

  //Location
  private static final int PERMISSIONS_REQUEST_CODE = 7171;
  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7171;
  private LocationRequest mLocationRequest;
  private Location mLastLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_online);

    listOnline = (RecyclerView) findViewById(R.id.listonline);
    listOnline.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
    listOnline.setLayoutManager(layoutManager);

    // set toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("Safe Tap Test");
    setSupportActionBar(toolbar);

    // Firebase
    locationsRef = FirebaseDatabase.getInstance().getReference("Locations");
    onlineRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
    currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


    // Check permissions
    int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
    int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (permission1 != PackageManager.PERMISSION_GRANTED && permission2 != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{
              Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION
      }, PERMISSIONS_REQUEST_CODE);
    } else if (checkPlayServices()){
      createLocationRequest();

      displayLocation();
    }

    setupSystem();

    updateList();
  }

  private void displayLocation() {
    // Check permissions
    int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
    int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (permission1 != PackageManager.PERMISSION_GRANTED && permission2 != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
    client.requestLocationUpdates(mLocationRequest, new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        mLastLocation = locationResult.getLastLocation();
        if (mLastLocation != null) {
          locationsRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                  .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                          FirebaseAuth.getInstance().getCurrentUser().getUid(),
                          String.valueOf(mLastLocation.getLatitude()),
                          String.valueOf(mLastLocation.getLongitude())));
        } else {
          Toast.makeText(ListOnlineActivity.this, "Couldn't get the Location", Toast.LENGTH_SHORT).show();
        }
      }
    }, null);
  }

  private void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(5000);
    mLocationRequest.setFastestInterval(3000);
    mLocationRequest.setSmallestDisplacement(10);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private boolean checkPlayServices() {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (apiAvailability.isUserResolvableError(resultCode)) {
        apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                .show();
      } else {
        Log.i(TAG, "This device is not supported");
        finish();
      }
      return false;
    }
    return true;
  }

  private void updateList() {
    FirebaseRecyclerOptions<User> options =
            new FirebaseRecyclerOptions.Builder<User>().setQuery(counterRef, User.class).build();
    mAdapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(options) {
      @Override
      protected void onBindViewHolder(@NonNull ListOnlineViewHolder holder, int position, @NonNull User model) {
        holder.txtEmail.setText(model.getEmail());
      }

      @NonNull
      @Override
      public ListOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_layout, parent, false);

        return new ListOnlineViewHolder(view);
      }
    };
    mAdapter.notifyDataSetChanged();
    listOnline.setAdapter(mAdapter);
  }

  public void setupSystem() {
    onlineRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue(Boolean.class)) {
          currentUserRef.onDisconnect().removeValue();

          counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                  .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));

          mAdapter.notifyDataSetChanged();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    counterRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
          User user = postSnapshot.getValue(User.class);
          Log.d("LOG", user.getEmail() + " is " + user.getStatus());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_join:
        counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
        break;
      case R.id.action_logout:
        currentUserRef.removeValue();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();
    mAdapter.startListening();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mAdapter.stopListening();
  }
}

package com.hexacta.trackonline.users;

import android.support.annotation.NonNull;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hexacta.trackonline.R;

public class ListOnlineActivity extends AppCompatActivity {

  // Firebase
  DatabaseReference onlineRef, currentUserRef, counterRef;
  FirebaseRecyclerAdapter<User, ListOnlineViewHolder> mAdapter;

  // View
  RecyclerView listOnline;
  RecyclerView.LayoutManager layoutManager;

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
    onlineRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
    currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    setupSystem();

    updateList();
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

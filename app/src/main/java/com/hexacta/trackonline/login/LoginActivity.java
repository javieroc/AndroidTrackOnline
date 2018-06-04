package com.hexacta.trackonline.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hexacta.trackonline.R;
import com.hexacta.trackonline.users.ListOnlineActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
  @BindView(R.id.email) EditText mEmailText;

  @BindView(R.id.password) EditText mPasswordText;

  @BindView(R.id.btn_login) Button mLoginButton;

  private FirebaseAuth mAuth;

  private static final String TAG = "EmailPassword";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    mAuth = FirebaseAuth.getInstance();
  }

  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
  }

  @OnClick(R.id.btn_login)
  public void onLogin() {
    final String email = mEmailText.getText().toString();
    final String password = mPasswordText.getText().toString();

    signIn(email, password);
  }

  private void signIn(final String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(TAG, "signInWithEmail:success");
          FirebaseUser user = mAuth.getCurrentUser();
          Toast.makeText(
            LoginActivity.this,
            "User with email: " + user.getEmail(),
            Toast.LENGTH_SHORT).show();

          Intent trackActivityIntent=new Intent(LoginActivity.this, ListOnlineActivity.class);
          startActivity(trackActivityIntent);
          finish();
        } else {
          // If sign in fails, display a message to the user.
          Log.w(TAG, "signInWithEmail:failure", task.getException());
          Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
          }
      });
  }
}

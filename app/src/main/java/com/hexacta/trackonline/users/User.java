package com.hexacta.trackonline.users;

public class User {
  private String email, status;

  public User() {
  }

  public String getStatus() {
    return status;
  }

  public User(String email, String status) {
    this.email = email;
    this.status = status;
  }

  public String getEmail() {
    return email;
  }
}

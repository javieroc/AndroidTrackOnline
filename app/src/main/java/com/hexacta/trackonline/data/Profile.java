package com.hexacta.trackonline.data;

public class Profile {

  String id;
  String name;
  String photo;
  String occupation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

  public Profile(String id, String name, String photo, String occupation) {
    this.id = id;
    this.name = name;
    this.photo = photo;
    this.occupation = occupation;
  }

  public Profile(String name, String photo, String occupation) {
    this.name = name;
    this.photo = photo;
    this.occupation = occupation;
  }
}

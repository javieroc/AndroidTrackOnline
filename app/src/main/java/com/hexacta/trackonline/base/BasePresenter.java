package com.hexacta.trackonline.base;

public interface BasePresenter<T> {

  //TODO We should refactor the Presenter

  /**
   * Binds presenter with a view when resumed. The Presenter will perform initialization here.
   *
   * @param view the view associated with this presenter
   */
  void takeView(T view);

  /**
   * Drops the reference to the view when destroyed
   */
  void dropView();

}


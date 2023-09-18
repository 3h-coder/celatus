package com.celatus;

/** Interface for objects that are meant to be saved in our database */
public interface Recordable {

  void calculateID();

  void saveRecord(String changes);
}

package com.google.appinventor.client.editor.youngandroid.events;

import com.google.gwt.core.client.JavaScriptObject;

public class DeleteComponent extends JavaScriptObject implements DesignerEvent {

  @Override
  public boolean recordUndo() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T as(Class<T> eventType) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getProjectId() {
    // TODO Auto-generated method stub
    return "";
  }

  @Override
  public boolean isRealtime() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setRealtime(boolean realtime) {
    // TODO Auto-generated method stub

  }

  public JavaScriptObject toJson() {
    return null;
  }
}

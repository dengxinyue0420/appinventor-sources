package com.google.appinventor.client;

import com.google.appinventor.client.editor.simple.components.FormChangeListener;
import com.google.appinventor.client.editor.simple.components.MockComponent;
import com.google.appinventor.client.editor.youngandroid.events.ChangeProperty;
import com.google.appinventor.client.editor.youngandroid.events.CreateComponent;
import com.google.appinventor.client.editor.youngandroid.events.DeleteComponent;
import com.google.appinventor.client.editor.youngandroid.events.EventFactory;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * This class manages group collaboration.
 */
public class CollaborationManager implements FormChangeListener {

  private boolean broadcast;
  // TODO(xinyue): Modify this to support multi screen
  public String screenChannel;

  public CollaborationManager() {
    broadcast = true;
    this.screenChannel = "";
    EventFactory.exportMethodToJavascript();
  }

  public void enableBroadcast() {
    broadcast = true;
  }

  public void disableBroadcast() {
    broadcast = false;
  }

  public void setScreenChannel(String screenChannel) {
    this.screenChannel = screenChannel;
  }

  public String getScreenChannel() {
    return this.screenChannel;
  }

  @Override
  public void onComponentPropertyChanged(MockComponent component, String propertyName, String propertyValue) {
    if(broadcast){
      ChangeProperty event = ChangeProperty.create(Long.toString(Ode.getInstance().getCurrentYoungAndroidProjectId()), component.getUuid(), propertyName, propertyValue);
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentRemoved(MockComponent component, boolean permanentlyDeleted) {
    if (broadcast) {
      DeleteComponent event = DeleteComponent.create(Long.toString(Ode.getInstance().getCurrentYoungAndroidProjectId()), component.getUuid(), component.getContainer().getUuid(), permanentlyDeleted);
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentAdded(MockComponent component) {
    if (broadcast) {
      CreateComponent event = CreateComponent.create(Long.toString(Ode.getInstance().getCurrentYoungAndroidProjectId()), component.getUuid(), component.getType(), component.getContainer().getUuid(), component.getIndex());
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentRenamed(MockComponent component, String oldName) {
    if(broadcast){
      ChangeProperty event = ChangeProperty.create(Long.toString(Ode.getInstance().getCurrentYoungAndroidProjectId()), component.getUuid(), MockComponent.PROPERTY_NAME_NAME, component.getName());
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentSelectionChange(MockComponent component, boolean selected) {

  }

  public native void broadcastComponentEvent(JavaScriptObject eventJson)/*-{
    var msg = {
      "channel": $wnd.Ode_getCurrentChannel(),
      "user": $wnd.userEmail,
      "source": "Designer",
      "event": eventJson
    };
    console.log(msg);
    $wnd.socket.emit("component", msg);
  }-*/;

  public native void componentSocketEvent(String channel)/*-{
    console.log("component socket event "+channel);
    $wnd.socket.emit("screenChannel", channel);
    $wnd.subscribedChannel.add(channel);
    var userLastSelection = new Map();
    $wnd.socket.on(channel, function(msg){
      var msgJSON = JSON.parse(msg);
      var event = msgJSON["event"];
      var userFrom = msgJSON["user"];
      if($wnd.userEmail != userFrom){
        console.log(msgJSON);
        switch(msgJSON["source"]) {
          case "Designer":
            $wnd.Ode_disableBroadcast();
            $wnd.EventFactory_run(event["type"], event);
            $wnd.Ode_enableBroadcast();
            break;
          case "Block":
            // TODO(xinyue): assign it to the correct workspace
            var workspace = Blockly.mainWorkspace;
            var newEvent = Blockly.Events.fromJson(event, workspace);
            Blockly.Events.disable();
            newEvent.run(true);
            if(event["type"]==="delete"){
              Blockly.Events.enable();
              return;
            }
            var color = $wnd.userColorMap.get(userFrom);
            var block = workspace.getBlockById(newEvent.blockId);
            if(event["type"]==="create"){
              block.initSvg();
              block.render();
            }
            if(userLastSelection.has(userFrom)){
              var prevSelected = userLastSelection.get(userFrom);
              prevSelected.svgGroup_.className.baseVal = 'blockDraggable';
              prevSelected.svgGroup_.className.animVal = 'blockDraggable';
              prevSelected.svgPath_.removeAttribute('stroke');
            }
            block.svgGroup_.className.baseVal += ' blocklyOtherSelected';
            block.svgGroup_.className.animVal += ' blocklyOtherSelected';
            block.svgPath_.setAttribute('stroke', color);
            userLastSelection.set(userFrom, block);
            Blockly.Events.enable();
            break;
        }
      }
    });
  }-*/;

  public native void connectCollaborationServer(String server, String userEmail) /*-{
    $wnd.socket = $wnd.io.connect(server, {autoConnect: true});
    $wnd.userEmail = userEmail;
    $wnd.colors = ['#999999','#f781bf','#a65628','#ffff33','#ff7f00','#984ea3','#4daf4a','#377eb8','#e41a1c'];
    $wnd.userColorMap = new $wnd.Map();
    $wnd.userColorMap.rmv = $wnd.userColorMap["delete"];
    $wnd.subscribedChannel = new $wnd.Set();
    $wnd.socket.emit("userChannel", userEmail);
    $wnd.socket.on(userEmail, function(msg){
      var msgJSON = JSON.parse(msg);
      var projectId = String(msgJSON["project"]);
      $wnd.Ode_addSharedProject(projectId);
    });
  }-*/;

  public native void joinProject(String projectId) /*-{
    $wnd.socket.emit("projectChannel", projectId);
    $wnd.project = projectId;
    $wnd.DesignToolbar_removeAllJoinedUser();
    var msg = {
      "project": projectId,
      "user": $wnd.userEmail
    };
    $wnd.socket.emit("userJoin", msg);
    $wnd.socket.on(projectId, function(msg){
      var msgJSON = JSON.parse(msg);
      if(msgJSON["project"]!=$wnd.project){
        return;
      }
      var c = "";
      var user = msgJSON["user"];
      if(user!==$wnd.userEmail){
        switch(msgJSON["type"]){
          case "join":
            if(!$wnd.userColorMap.has(user)){
              c = $wnd.colors.pop();
              $wnd.userColorMap.set(user, c);
            }
            $wnd.DesignToolbar_addJoinedUser(user, $wnd.userColorMap.get(user));
            break;
          case "leave":
            if($wnd.userColorMap.has(user)){
              c = $wnd.userColorMap.get(user);
              $wnd.colors.push(c);
              $wnd.userColorMap.rmv(user);
            }
            $wnd.DesignToolbar_removeJoinedUser(user);
            break;
        }
      }
    });
  }-*/;

  public native void leaveProject()/*-{
    var msg = {
      "project": $wnd.project,
      "user": $wnd.userEmail
    };
    $wnd.project = "";
    $wnd.socket.emit("userLeave", msg);
  }-*/;

}

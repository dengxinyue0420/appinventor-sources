package com.google.appinventor.client;

import com.google.appinventor.client.editor.simple.components.FormChangeListener;
import com.google.appinventor.client.editor.simple.components.MockComponent;
import com.google.appinventor.client.editor.youngandroid.events.*;
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
      ChangeProperty event = ChangeProperty.create(Ode.getCurrentChannel(), component.getUuid(), propertyName, propertyValue);
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentRemoved(MockComponent component, boolean permanentlyDeleted) {
    if (broadcast) {
      DeleteComponent event = DeleteComponent.create(Ode.getCurrentChannel(), component.getUuid());
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentAdded(MockComponent component) {
    if (broadcast) {
      CreateComponent event = CreateComponent.create(Ode.getCurrentChannel(), component.getUuid(), component.getType());
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentRenamed(MockComponent component, String oldName) {
    if(broadcast){
      ChangeProperty event = ChangeProperty.create(Ode.getCurrentChannel(), component.getUuid(), MockComponent.PROPERTY_NAME_NAME, component.getName());
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentMoved(MockComponent component, String newParentId, int index) {
    if(broadcast){
      MoveComponent event = MoveComponent.create(Ode.getCurrentChannel(), component.getUuid(), newParentId, index);
      broadcastComponentEvent(event.toJson());
    }
  }

  @Override
  public void onComponentSelectionChange(MockComponent component, boolean selected) {
    if (component.isForm()) {
      return;
    }
    if (broadcast) {
      SelectComponent event = SelectComponent.create(
          Ode.getCurrentChannel(), component.getUuid(), Ode.getInstance().getUser().getUserEmail(), selected);
      broadcastComponentEvent(event.toJson());
    }
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
    var workspace = Blockly.allWorkspaces[channel];
    // userLastSelection is the latest block selected by other user, mapped by user email
    // lockedComponent is the components locked by other users. Key is the component id, value is userEmail and timestamp
    // lockedBlock is the block locked by other users. Key is the block id, value is userEmail and timestamp
    workspace.userLastSelection = new Map();
    workspace.lockedComponent = {};
    workspace.lockedBlock = {};
    $wnd.socket.on(channel, function(msg){
      var msgJSON = JSON.parse(msg);
      var event = msgJSON["event"];
      var userFrom = msgJSON["user"];
      if($wnd.userEmail != userFrom){
        console.log(msgJSON);
        switch(msgJSON["source"]) {
          case "Designer":
            var componentEvent = AI.Events.ComponentEvent.fromJson(event);
            $wnd.Ode_disableBroadcast();
            componentEvent.run();
            $wnd.Ode_enableBroadcast();
            break;
          case "Block":
            var newEvent = Blockly.Events.fromJson(event, workspace);
            Blockly.Events.disable();
            newEvent.run(true);
            if(event["type"]==="delete"){
              Blockly.Events.enable();
              return;
            }
            var color = $wnd.userColorMap.get($wnd.project).get(userFrom);
            var block = workspace.getBlockById(newEvent.blockId);
            if(event["type"]==="create"){
              if (workspace.rendered) {
                if(workspace.getParentSvg().parentNode.offsetParent){
                  block.initSvg();
                  block.render();
                } else {
                  workspace.blocksNeedingRendering.push(block);
                }
              } else {
                workspace.blocksNeedingRendering.push(block);
              }

            }
            if(workspace.userLastSelection.has(userFrom)){
              var prevSelected = workspace.userLastSelection.get(userFrom);
              if(prevSelected.svgGroup_){
                prevSelected.svgGroup_.className.baseVal = 'blockDraggable';
                prevSelected.svgGroup_.className.animVal = 'blockDraggable';
                prevSelected.svgPath_.removeAttribute('stroke');
              }
            }
            if(block.svgGroup_) {
              block.svgGroup_.className.baseVal += ' blocklyOtherSelected';
              block.svgGroup_.className.animVal += ' blocklyOtherSelected';
              block.svgPath_.setAttribute('stroke', color);
            }
            workspace.userLastSelection.set(userFrom, block);
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
    if(!$wnd.userColorMap.has(projectId)){
      $wnd.userColorMap.set(projectId, new $wnd.Map());
      $wnd.userColorMap.get(projectId).rmv = $wnd.userColorMap.get(projectId)["delete"];
    }
    $wnd.socket.on(projectId, function(msg){
      var msgJSON = JSON.parse(msg);
      if(msgJSON["project"]!=$wnd.project){
        return;
      }
      var c = "";
      var user = msgJSON["user"];
      var colorMap = $wnd.userColorMap.get(msgJSON["project"]);
      if(user!==$wnd.userEmail){
        switch(msgJSON["type"]){
          case "join":
            if(!colorMap.has(user)){
              c = $wnd.colors.pop();
              colorMap.set(user, c);
            }
            $wnd.DesignToolbar_addJoinedUser(user, colorMap.get(user));
            if(Blockly.mainWorkspace.getParentSvg()
              && !Blockly.mainWorkspace.getParentSvg().getElementById("blocklyLockedPattern-"+user)){
                Blockly.Collaboration.createPattern(user, $wnd.userColorMap.get(msgJSON["project"]).get(user));
            }
            break;
          case "leave":
            if(colorMap.has(user)){
              c = colorMap.get(user);
              $wnd.colors.push(c);
              colorMap.rmv(user);
            }
            $wnd.DesignToolbar_removeJoinedUser(user);
            break;
          case "leader":
            $wnd.DesignToolbar_switchLeader(msgJSON["project"], msgJSON["leader"], msgJSON["leaderEmail"]);
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

  public native void switchLeader(String leaderId, String leaderEmail)/*-{
    var msg = {
      "project": $wnd.project,
      "user": $wnd.userEmail,
      "leader": leaderId,
      "leaderEmail": leaderEmail
    }
    $wnd.socket.emit("leader", msg);
  }-*/;
}

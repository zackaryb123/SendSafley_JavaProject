package com.sendsafely;

import java.util.ArrayList;

public class Command {
    private ArrayList<String> actionList = new ArrayList();

    public enum EventTypes {
        FILE,
        RECIPIENT,
        NOTIFICATION
    }

    public void undo(SendSafely sendSafely, Package pkgInfo, Recipient recipient, File file) {
        /* Get last action in list */
        EventTypes cmd = EventTypes.valueOf(actionList.get(actionList.size() - 1));
        if (Utils.resolve(() -> cmd).isPresent()) {
             switch (cmd) {
                 case FILE:
                     Helper.removeFileFromPackage(sendSafely, pkgInfo, file);
                     actionList.remove(actionList.size() - 1);
                     return;
                 case RECIPIENT:
                     Helper.removeRecipient(sendSafely, pkgInfo, recipient);
                     actionList.remove(actionList.size() - 1);
                     return;
                 default:
                     System.out.println("All actions have been undone!");
                     return;
             }
        }
    }

    public void trackAction(EventTypes action) {
        actionList.add(action.name());
    }

    public ArrayList<String> getActionList() {
        return actionList;
    }

    public void setActionList(ArrayList<String> actionList) {
        this.actionList = actionList;
    }

}
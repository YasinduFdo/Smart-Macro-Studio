package com.smartmacro.model;


public class AutomationAction {

    private static int NEXT_ID = 1;

    private final int id;
    private ActionType actionType;

    private int mouseX;
    private int mouseY;
    private int wheelAmount;

    private String keyValue; 
    private String text;     

    private long timestamp;   
    private long delay;        

    private boolean enabled = true;

    private String imageReference; 
    private String windowTitle;    

    public AutomationAction previous;
    public AutomationAction next;

    public AutomationAction(ActionType actionType) {
        this.id = NEXT_ID++;
        this.actionType = actionType;
        this.timestamp = System.currentTimeMillis();
    }

    public AutomationAction(int id, ActionType actionType) {
        this.id = id;
        this.actionType = actionType;
        if (id >= NEXT_ID) NEXT_ID = id + 1;
    }

    public static void resetIdCounter(int startAt) {
        NEXT_ID = startAt;
    }

    public int getId() { return id; }

    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }

    public int getMouseX() { return mouseX; }
    public void setMouseX(int mouseX) { this.mouseX = mouseX; }

    public int getMouseY() { return mouseY; }
    public void setMouseY(int mouseY) { this.mouseY = mouseY; }

    public int getWheelAmount() { return wheelAmount; }
    public void setWheelAmount(int wheelAmount) { this.wheelAmount = wheelAmount; }

    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public long getDelay() { return delay; }
    public void setDelay(long delay) { this.delay = delay; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getImageReference() { return imageReference; }
    public void setImageReference(String imageReference) { this.imageReference = imageReference; }

    public String getWindowTitle() { return windowTitle; }
    public void setWindowTitle(String windowTitle) { this.windowTitle = windowTitle; }

    public String describe() {
        switch (actionType) {
            case MOUSE_MOVE:   return "Move to (" + mouseX + ", " + mouseY + ")";
            case LEFT_CLICK:   return "Left click at (" + mouseX + ", " + mouseY + ")";
            case RIGHT_CLICK:  return "Right click at (" + mouseX + ", " + mouseY + ")";
            case MIDDLE_CLICK: return "Middle click at (" + mouseX + ", " + mouseY + ")";
            case MOUSE_WHEEL:  return "Wheel " + wheelAmount;
            case KEY_DOWN:     return "Key down [" + keyValue + "]";
            case KEY_UP:       return "Key up [" + keyValue + "]";
            case SPECIAL_KEY:  return "Special key [" + keyValue + "]";
            case TYPE_TEXT:    return "Type: \"" + text + "\"";
            case DELAY:        return "Wait " + delay + "ms";
            default:           return actionType.toString();
        }
    }

    @Override
    public String toString() {
        return "#" + id + " " + actionType + " " + describe();
    }
}
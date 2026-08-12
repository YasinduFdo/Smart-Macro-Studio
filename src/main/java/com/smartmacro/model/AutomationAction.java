package com.smartmacro.model;

/**
 * A single recorded event. This class doubles as the NODE of the custom
 * Doubly Linked List (see com.smartmacro.datastructure.ActionLinkedList) -
 * exactly as specified in the coursework brief: {@code previous} / {@code next}
 * live directly on the object instead of a separate wrapper node class.
 */
public class AutomationAction {

    private static int NEXT_ID = 1;

    private final int id;
    private ActionType actionType;

    private int mouseX;
    private int mouseY;
    private int wheelAmount;

    private String keyValue;   // e.g. "ENTER", "CTRL", single character keys, etc.
    private String text;       // literal text

    private long timestamp;    // ms since epoch, when the event was captured
    private long delay;        // ms since the previous event

    private boolean enabled = true;

    private String imageReference; // reserved for future image-based matching
    private String windowTitle;    // optional

    // --- Doubly Linked List pointers ---
    public AutomationAction previous;
    public AutomationAction next;

    public AutomationAction(ActionType actionType) {
        this.id = NEXT_ID++;
        this.actionType = actionType;
        this.timestamp = System.currentTimeMillis();
    }

    /** Used only when rebuilding actions from a saved JSON file, to keep the same id. */
    public AutomationAction(int id, ActionType actionType) {
        this.id = id;
        this.actionType = actionType;
        if (id >= NEXT_ID) NEXT_ID = id + 1;
    }

    public static void resetIdCounter(int startAt) {
        NEXT_ID = startAt;
    }

    // ----- getters / setters -----
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

    /** One-line human readable description, used by the Timeline Editor table. */
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
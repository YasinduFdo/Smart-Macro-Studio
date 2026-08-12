package com.smartmacro.model;

/**
 * Every kind of event the Recorder Engine can capture and the
 * Playback Engine / Script Generator can replay.
 */
public enum ActionType {
    MOUSE_MOVE,
    LEFT_CLICK,
    RIGHT_CLICK,
    MIDDLE_CLICK,
    MOUSE_WHEEL,
    KEY_DOWN,
    KEY_UP,
    TYPE_TEXT,
    SPECIAL_KEY,
    DELAY
}

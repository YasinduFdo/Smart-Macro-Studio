package com.smartmacro.recorder;

import com.smartmacro.datastructure.ActionLinkedList;
import com.smartmacro.model.ActionType;
import com.smartmacro.model.AutomationAction;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalHookRecorder implements NativeMouseInputListener, NativeKeyListener {

    private ActionLinkedList list;
    private boolean recording = false;
    private long lastTime = 0;
    private boolean isHookSetup = false; 

    public GlobalHookRecorder(ActionLinkedList list) {
        this.list = list;
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    public void setList(ActionLinkedList list) {
        this.list = list;
    }

    public void start() {
        if (recording) return;
        try {
            if (!isHookSetup) {
                if (!GlobalScreen.isNativeHookRegistered()) {
                    GlobalScreen.registerNativeHook();
                }
                GlobalScreen.addNativeMouseListener(this);
                GlobalScreen.addNativeMouseMotionListener(this);
                GlobalScreen.addNativeKeyListener(this);
                isHookSetup = true;
            }

            recording = true; 
            lastTime = System.currentTimeMillis();
            System.out.println("[GlobalHookRecorder] Global recording started.");
        } catch (NativeHookException e) {
            System.err.println("[GlobalHookRecorder] Error starting native hook: " + e.getMessage());
        }
    }

    public void stop() {
        if (!recording) return;
        
        recording = false; 
        
        System.out.println("[GlobalHookRecorder] Global recording stopped. Total steps: " + (list != null ? list.size() : 0));
    }

    private void addMouseAction(ActionType type, int x, int y) {
        if (list == null) return;
        
        long now = System.currentTimeMillis();
        long delay = lastTime == 0 ? 0 : now - lastTime;
        lastTime = now;

        AutomationAction action = new AutomationAction(type);
        action.setMouseX(x);
        action.setMouseY(y);
        action.setDelay(delay);

        list.insert(action);
    }

    private void addKeyAction(ActionType type, String keyValue) {
        if (list == null) return;
        
        long now = System.currentTimeMillis();
        long delay = lastTime == 0 ? 0 : now - lastTime;
        lastTime = now;

        AutomationAction action = new AutomationAction(type);
        action.setKeyValue(keyValue);
        action.setDelay(delay);

        list.insert(action);
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        // This is for avoid missed inputs if the mouse moves when clicking
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        if (!recording) return;

        System.out.println("DEBUG - Mouse Button Pressed: " + e.getButton());
        ActionType type;
        
        if (e.getButton() == 2) {
            type = ActionType.RIGHT_CLICK;
        } else if (e.getButton() == 3) {
            type = ActionType.MIDDLE_CLICK;
        } else {
            type = ActionType.LEFT_CLICK;
        }
        
        addMouseAction(type, e.getX(), e.getY());
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (!recording) return;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (!recording) return;
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        addKeyAction(ActionType.KEY_DOWN, keyText);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (!recording) return;
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        addKeyAction(ActionType.KEY_UP, keyText);
    }

    @Override public void nativeMouseMoved(NativeMouseEvent e) {}
    @Override public void nativeMouseDragged(NativeMouseEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}
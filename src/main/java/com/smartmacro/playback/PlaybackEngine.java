package com.smartmacro.playback;

import com.smartmacro.datastructure.ActionLinkedList;
import com.smartmacro.model.AutomationAction;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Traverses the ActionLinkedList (forward, head to tail) and physically
 * reproduces each enabled action using java.awt.Robot: real mouse moves,
 * real clicks, real key presses. Delays between actions are respected and
 * scaled by the chosen playback speed.
 */
public class PlaybackEngine {

    public enum State { PLAYING, PAUSED, STOPPED, FINISHED }

    private final ActionLinkedList list;
    private final AtomicReference<State> state = new AtomicReference<>(State.STOPPED);
    private double speedMultiplier = 1.0;
    private long lastPlaybackDurationMs = 0;

    public PlaybackEngine(ActionLinkedList list) {
        this.list = list;
    }

    public void setSpeedMultiplier(double speed) {
        if (speed <= 0) throw new IllegalArgumentException("speed must be > 0");
        this.speedMultiplier = speed;
    }

    public double getSpeedMultiplier() { return speedMultiplier; }
    public State getState() { return state.get(); }
    public long getLastPlaybackDurationMs() { return lastPlaybackDurationMs; }

    public void pause() { state.compareAndSet(State.PLAYING, State.PAUSED); }
    public void resume() { state.compareAndSet(State.PAUSED, State.PLAYING); }
    public void stop() { state.set(State.STOPPED); }

    public void play() {
        Robot robot;
        try {
            robot = new Robot();
            robot.setAutoWaitForIdle(true);
        } catch (AWTException e) {
            System.out.println("! Could not start Robot (no display / headless environment?): " + e.getMessage());
            System.out.println("  Playback will run in DRY-RUN mode (prints actions instead of executing them).");
            robot = null;
        }

        state.set(State.PLAYING);
        long start = System.currentTimeMillis();

        // Use the new academic Linked List traversal method!
        List<AutomationAction> actionsToPlay = list.forwardTraversal();
        
        for (AutomationAction current : actionsToPlay) {
            // Handle pause / stop between every single action.
            while (state.get() == State.PAUSED) {
                sleepQuiet(50);
            }
            if (state.get() == State.STOPPED) {
                System.out.println("Playback stopped by user.");
                break;
            }

            if (current.isEnabled()) {
                long scaledDelay = (long) (current.getDelay() / speedMultiplier);
                if (scaledDelay > 0) sleepQuiet(scaledDelay);
                execute(robot, current);
            }
        }

        lastPlaybackDurationMs = System.currentTimeMillis() - start;
        if (state.get() != State.STOPPED) {
            state.set(State.FINISHED);
            System.out.println("Playback finished in " + lastPlaybackDurationMs + "ms.");
        }
    }

    private void execute(Robot robot, AutomationAction action) {
        String resolvedText = resolveText(action);

        switch (action.getActionType()) {
            case MOUSE_MOVE:
                if (robot != null) robot.mouseMove(action.getMouseX(), action.getMouseY());
                else logDryRun(action);
                break;
            case LEFT_CLICK:
                if (robot != null) {
                    moveIfSet(robot, action);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                } else logDryRun(action);
                break;
            case RIGHT_CLICK:
                if (robot != null) {
                    moveIfSet(robot, action);
                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                } else logDryRun(action);
                break;
            case MIDDLE_CLICK:
                if (robot != null) {
                    moveIfSet(robot, action);
                    robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                } else logDryRun(action);
                break;
            case MOUSE_WHEEL:
                if (robot != null) robot.mouseWheel(action.getWheelAmount());
                else logDryRun(action);
                break;
            case KEY_DOWN:
                if (robot != null) robot.keyPress(mapKey(action.getKeyValue()));
                else logDryRun(action);
                break;
            case KEY_UP:
                if (robot != null) robot.keyRelease(mapKey(action.getKeyValue()));
                else logDryRun(action);
                break;
            case SPECIAL_KEY:
                if (robot != null) {
                    int code = mapKey(action.getKeyValue());
                    robot.keyPress(code);
                    robot.keyRelease(code);
                } else logDryRun(action);
                break;
            case TYPE_TEXT:
                if (robot != null) typeString(robot, resolvedText);
                else System.out.println("  [dry-run] type: " + resolvedText);
                break;
            case DELAY:
                // delay already applied above via scaledDelay
                break;
        }
    }

    private void moveIfSet(Robot robot, AutomationAction action) {
        if (action.getMouseX() != 0 || action.getMouseY() != 0) {
            robot.mouseMove(action.getMouseX(), action.getMouseY());
        }
    }

    private String resolveText(AutomationAction action) {
        return action.getText() == null ? "" : action.getText();
    }

    private void typeString(Robot robot, String text) {
        for (char c : text.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (keyCode == KeyEvent.VK_UNDEFINED) continue;
            boolean needsShift = Character.isUpperCase(c) || "!@#$%^&*()_+{}|:\"<>?~".indexOf(c) >= 0;
            try {
                if (needsShift) robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                if (needsShift) robot.keyRelease(KeyEvent.VK_SHIFT);
            } catch (IllegalArgumentException ignored) {
                // unsupported char for direct Robot typing - skip it
            }
        }
    }

    private int mapKey(String keyValue) {
        if (keyValue == null) return KeyEvent.VK_UNDEFINED;
        try {
            return switch (keyValue.toUpperCase()) {
                case "ENTER" -> KeyEvent.VK_ENTER;
                case "TAB" -> KeyEvent.VK_TAB;
                case "ESC", "ESCAPE" -> KeyEvent.VK_ESCAPE;
                case "CTRL", "CONTROL" -> KeyEvent.VK_CONTROL;
                case "ALT" -> KeyEvent.VK_ALT;
                case "SHIFT" -> KeyEvent.VK_SHIFT;
                case "SPACE" -> KeyEvent.VK_SPACE;
                case "BACKSPACE" -> KeyEvent.VK_BACK_SPACE;
                case "DELETE", "DEL" -> KeyEvent.VK_DELETE;
                case "UP" -> KeyEvent.VK_UP;
                case "DOWN" -> KeyEvent.VK_DOWN;
                case "LEFT" -> KeyEvent.VK_LEFT;
                case "RIGHT" -> KeyEvent.VK_RIGHT;
                case "F1" -> KeyEvent.VK_F1;
                case "F2" -> KeyEvent.VK_F2;
                case "F3" -> KeyEvent.VK_F3;
                case "F4" -> KeyEvent.VK_F4;
                case "F5" -> KeyEvent.VK_F5;
                default -> {
                    if (keyValue.length() == 1) yield KeyEvent.getExtendedKeyCodeForChar(keyValue.charAt(0));
                    yield KeyEvent.VK_UNDEFINED;
                }
            };
        } catch (Exception e) {
            return KeyEvent.VK_UNDEFINED;
        }
    }

    private void logDryRun(AutomationAction action) {
        System.out.println("  [dry-run] " + action.describe());
    }

    private void sleepQuiet(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
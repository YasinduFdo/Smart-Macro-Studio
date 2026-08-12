package com.smartmacro.undoredo;

import java.util.Stack;

/**
 * Classic two-stack undo/redo. Every mutating Timeline Editor operation
 * (delete, move, edit, insert, disable...) is wrapped as a Command and
 * pushed here after being executed; undo() pops it and calls Command.undo(),
 * pushing it onto the redo stack, and vice-versa. Executing a brand new
 * command clears the redo stack (standard behaviour).
 */
public class UndoRedoManager {

    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * Executes a command and records it so it can be undone.
     *
     * Commands that report no change are NOT recorded. This matters: moveUp on
     * the first step (or moveDown on the last) is a no-op, and recording it
     * would let a later undo call moveDown on a node that never moved, quietly
     * corrupting the timeline order.
     */
    public void doCommand(Command command) {
        if (command == null) return;
        
        // If the command fails to execute (e.g. moving up the head node), abort recording
        if (!command.execute()) {
            return; 
        }
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    /** @return description of what was undone, or null if there was nothing to undo. */
    public String undo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        Command c = undoStack.pop();
        c.undo();
        redoStack.push(c);
        return c.describe();
    }

    /** @return description of what was redone, or null if there was nothing to redo. */
    public String redo() {
        if (redoStack.isEmpty()) {
            return null;
        }
        Command c = redoStack.pop();
        c.execute();
        undoStack.push(c);
        return c.describe();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
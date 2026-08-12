package com.smartmacro.undoredo;

import java.util.Stack;


public class UndoRedoManager {

    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    
    public void doCommand(Command command) {
        if (command == null) return;
        
        
        if (!command.execute()) {
            return; 
        }
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    
    public String undo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        Command c = undoStack.pop();
        c.undo();
        redoStack.push(c);
        return c.describe();
    }

   
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
package com.smartmacro.undoredo;

/** A reversible operation performed on the timeline's ActionLinkedList. */
public interface Command {
    
    /** 
     * @return true if the command actually changed state, false if it did nothing 
     * (e.g., trying to move the top item up).
     */
    boolean execute();
    
    void undo();
    
    /** Short human-readable label shown in undo/redo history, e.g. "Delete step #4". */
    String describe();
}
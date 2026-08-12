package com.smartmacro.undoredo;


public interface Command {
    
   
    boolean execute();
    
    void undo();
    
    
    String describe();
}
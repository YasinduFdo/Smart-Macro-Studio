package com.smartmacro.undoredo;

import com.smartmacro.datastructure.ActionLinkedList;
import com.smartmacro.model.AutomationAction;

/**
 * Factory class containing Command implementations for timeline editing actions.
 */
public class TimelineCommands {

    public static Command delete(ActionLinkedList list, AutomationAction node) {
        return new Command() {
            private final AutomationAction prev = node.previous;
            @Override
            public boolean execute() {
                return list.deleteNode(node);
            }
            @Override
            public void undo() {
                if (prev == null) {
                    list.insertFirst(node);
                } else {
                    list.insertAfter(prev, node);
                }
            }
            @Override
            public String describe() {
                return "Delete node";
            }
        };
    }

    public static Command toggleEnabled(AutomationAction node) {
        return new Command() {
            @Override
            public boolean execute() {
                node.setEnabled(!node.isEnabled());
                return true;
            }
            @Override
            public void undo() {
                node.setEnabled(!node.isEnabled());
            }
            @Override
            public String describe() {
                return "Toggle enabled state";
            }
        };
    }

    public static Command moveUp(ActionLinkedList list, AutomationAction node) {
        return new Command() {
            @Override
            public boolean execute() {
                return list.moveUp(node);
            }
            @Override
            public void undo() {
                list.moveDown(node);
            }
            @Override
            public String describe() {
                return "Move step up";
            }
        };
    }

    public static Command moveDown(ActionLinkedList list, AutomationAction node) {
        return new Command() {
            @Override
            public boolean execute() {
                return list.moveDown(node);
            }
            @Override
            public void undo() {
                list.moveUp(node);
            }
            @Override
            public String describe() {
                return "Move step down";
            }
        };
    }
}
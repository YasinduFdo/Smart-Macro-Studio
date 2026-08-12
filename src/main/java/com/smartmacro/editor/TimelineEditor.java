package com.smartmacro.editor;

import com.smartmacro.datastructure.ActionLinkedList;
import com.smartmacro.model.ActionType;
import com.smartmacro.model.AutomationAction;
import com.smartmacro.undoredo.Command;
import com.smartmacro.undoredo.TimelineCommands;
import com.smartmacro.undoredo.UndoRedoManager;

import java.util.List;
import java.util.Locale;

public class TimelineEditor {

    private ActionLinkedList list;
    private final UndoRedoManager undoRedo;

    public TimelineEditor(ActionLinkedList list, UndoRedoManager undoRedo) {
        this.list = list;
        this.undoRedo = undoRedo;
    }

    public void rebind(ActionLinkedList newList) {
        this.list = newList;
    }

    public void printTable() {
        List<AutomationAction> actions = list.forwardTraversal();
        if (actions.isEmpty()) {
            System.out.println("(timeline is empty)");
            return;
        }

        System.out.println("\n Step | Action Type    | Delay(ms) | Enabled | Description");
        System.out.println("------+----------------+-----------+---------+---------------------------------");
        int step = 1;
        for (AutomationAction a : actions) {
            System.out.printf(" %-4d | %-14s | %-9d | %-7s | %s%n",
                    step++,
                    a.getActionType(),
                    a.getDelay(),
                    a.isEnabled() ? "yes" : "NO",
                    a.describe());
        }
        System.out.println();
    }

    public AutomationAction findByStepNumber(int stepNumber) {
        if (stepNumber < 1 || stepNumber > list.size()) return null;
        return list.getAt(stepNumber - 1);
    }

    public boolean delete(int stepNumber) {
        AutomationAction node = findByStepNumber(stepNumber);
        if (node == null) return false;
        undoRedo.doCommand(TimelineCommands.delete(list, node));
        return true;
    }

    public boolean duplicate(int stepNumber) {
        AutomationAction original = findByStepNumber(stepNumber);
        if (original == null) return false;
        AutomationAction copy = new AutomationAction(original.getActionType());
        copy.setMouseX(original.getMouseX());
        copy.setMouseY(original.getMouseY());
        copy.setWheelAmount(original.getWheelAmount());
        copy.setKeyValue(original.getKeyValue());
        copy.setText(original.getText());
        copy.setDelay(original.getDelay());
        copy.setEnabled(original.isEnabled());
        copy.setWindowTitle(original.getWindowTitle());

        Command insertAfterCmd = new Command() {
            @Override
            public boolean execute() {
                list.insertAfter(original, copy);
                return true;
            }
            @Override
            public void undo() {
                list.deleteNode(copy);
            }
            @Override
            public String describe() {
                return "Duplicate step #" + original.getId();
            }
        };
        undoRedo.doCommand(insertAfterCmd);
        return true;
    }

    public boolean toggleEnabled(int stepNumber) {
        AutomationAction node = findByStepNumber(stepNumber);
        if (node == null) return false;
        undoRedo.doCommand(TimelineCommands.toggleEnabled(node));
        return true;
    }

    public boolean moveUp(int stepNumber) {
        AutomationAction node = findByStepNumber(stepNumber);
        if (node == null) return false;
        undoRedo.doCommand(TimelineCommands.moveUp(list, node));
        return true;
    }

    public boolean moveDown(int stepNumber) {
        AutomationAction node = findByStepNumber(stepNumber);
        if (node == null) return false;
        undoRedo.doCommand(TimelineCommands.moveDown(list, node));
        return true;
    }

    public boolean insertDelay(int afterStepNumber, long delayMs) {
        final AutomationAction target = findByStepNumber(afterStepNumber);
        final AutomationAction node = new AutomationAction(ActionType.DELAY);
        node.setDelay(delayMs);
        Command cmd = new Command() {
            @Override
            public boolean execute() {
                if (target == null) {
                    list.insertFirst(node);
                } else {
                    list.insertAfter(target, node);
                }
                return true;
            }
            @Override
            public void undo() {
                list.deleteNode(node);
            }
            @Override
            public String describe() {
                return "Insert delay after step " + afterStepNumber;
            }
        };
        undoRedo.doCommand(cmd);
        return true;
    }

    public String undo() {
        return undoRedo.undo();
    }

    public String redo() {
        return undoRedo.redo();
    }

    public List<Integer> search(String query) {
        List<AutomationAction> actions = list.forwardTraversal();
        List<Integer> matches = new java.util.ArrayList<>();
        String q = query.toLowerCase(Locale.ROOT);

        for (int i = 0; i < actions.size(); i++) {
            AutomationAction a = actions.get(i);
            boolean match = false;

            if (String.valueOf(i + 1).equals(query)) match = true;
            if (a.getActionType().name().toLowerCase(Locale.ROOT).contains(q)) match = true;
            if (a.getText() != null && a.getText().toLowerCase(Locale.ROOT).contains(q)) match = true;
            if (a.getKeyValue() != null && a.getKeyValue().toLowerCase(Locale.ROOT).contains(q)) match = true;

            if (match) matches.add(i + 1);
        }
        return matches;
    }

    public void printHighlighted(List<Integer> matchingSteps) {
        List<AutomationAction> actions = list.forwardTraversal();
        if (matchingSteps.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }
        System.out.println("\nSearch results:");
        for (int step : matchingSteps) {
            AutomationAction a = actions.get(step - 1);
            System.out.println("  >> Step " + step + ": " + a.describe());
        }
        System.out.println();
    }
}
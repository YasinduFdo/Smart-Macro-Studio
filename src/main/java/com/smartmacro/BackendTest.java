package com.smartmacro;

import com.smartmacro.datastructure.ActionLinkedList;
import com.smartmacro.editor.TimelineEditor;
import com.smartmacro.model.ActionType;
import com.smartmacro.model.AutomationAction;
import com.smartmacro.undoredo.UndoRedoManager;

public class BackendTest {

    public static void main(String[] args) {
        System.out.println("=== STARTING SMART MACRO BACKEND TEST ===");

        // 1. Initialize core components
        ActionLinkedList list = new ActionLinkedList();
        UndoRedoManager undoRedo = new UndoRedoManager();
        TimelineEditor editor = new TimelineEditor(list, undoRedo);

        // 2. Add some initial dummy actions to the list
        System.out.println("\n-> Adding 3 initial actions...");
        AutomationAction a1 = new AutomationAction(ActionType.LEFT_CLICK);
        a1.setMouseX(500); a1.setMouseY(300);
        list.insert(a1);

        AutomationAction a2 = new AutomationAction(ActionType.TYPE_TEXT);
        a2.setText("Testing Data Structures");
        list.insert(a2);

        AutomationAction a3 = new AutomationAction(ActionType.KEY_DOWN);
        a3.setKeyValue("ENTER");
        list.insert(a3);

        // Print the initial table
        editor.printTable();

        // 3. Test the TimelineEditor (Duplicate Step 2)
        System.out.println("-> Duplicating Step 2...");
        editor.duplicate(2);
        editor.printTable();

        // 4. Test Undo (Should remove the duplicated step)
        System.out.println("-> Triggering UNDO...");
        System.out.println("Command Result: " + editor.undo());
        editor.printTable();

        // 5. Test Redo (Should bring the duplicated step back)
        System.out.println("-> Triggering REDO...");
        System.out.println("Command Result: " + editor.redo());
        editor.printTable();

        System.out.println("=== BACKEND TEST COMPLETED ===");
    }
}
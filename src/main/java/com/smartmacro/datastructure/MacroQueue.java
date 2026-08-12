package com.smartmacro.datastructure;

public class MacroQueue {
    
    
    int max_size;
    int front;
    int rear;
    
    
    String[] macroNames;
    ActionLinkedList[] macroLists;

    public MacroQueue() {
        this.max_size = 100; 
        this.front = 0;
        this.rear = -1;
        
        this.macroNames = new String[max_size];
        this.macroLists = new ActionLinkedList[max_size];
    }

    
    public void enqueue(String name, ActionLinkedList list) {
        if (rear == max_size - 1) {
            System.out.println("Full");
        } else {
            rear++;
            macroNames[rear] = name;
            macroLists[rear] = list;
        }
    }

    
    public ActionLinkedList dequeue() {
        if (front > rear) {
            System.out.println("Empty");
            return null;
        } else {
            ActionLinkedList listToExecute = macroLists[front];
            front++;
            return listToExecute;
        }
    }

    
    public boolean isEmpty() {
        return front > rear;
    }
}

package com.smartmacro.datastructure;

public class MacroQueue {
    
    // Core queue variables mapped directly to your lecture notes
    int max_size;
    int front;
    int rear;
    
    // Parallel arrays to hold the queued data
    String[] macroNames;
    ActionLinkedList[] macroLists;

    public MacroQueue() {
        this.max_size = 100; // Defining a maximum queue size
        this.front = 0;
        this.rear = -1;
        
        this.macroNames = new String[max_size];
        this.macroLists = new ActionLinkedList[max_size];
    }

    // Matches the 'enque' logic: checks if full, increments rear, inserts data
    public void enqueue(String name, ActionLinkedList list) {
        if (rear == max_size - 1) {
            System.out.println("Full");
        } else {
            rear++;
            macroNames[rear] = name;
            macroLists[rear] = list;
        }
    }

    // Matches the 'deque' logic: checks if empty, retrieves data at front, increments front
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

    // Helper method used by App.java to know when the queue is finished executing
    public boolean isEmpty() {
        return front > rear;
    }
}
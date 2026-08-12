package com.smartmacro.datastructure;

import com.smartmacro.model.AutomationAction;

public class Node {
    public AutomationAction data;
    public Node next;
    public Node prev; 

    public Node(AutomationAction data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
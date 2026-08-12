package com.smartmacro.datastructure;

import com.smartmacro.model.AutomationAction;
import java.util.ArrayList;
import java.util.List;

public class ActionLinkedList {
    
    public Node head;

    public ActionLinkedList() {
        head = null;
    }

    // Matches your lecturer's exact insertion logic!
    public void insert(AutomationAction data) {
        Node node = new Node(data);

        if (head == null) {
            head = node;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = node;
            node.prev = temp; 
        }
    }

    public void insertAt(int index, AutomationAction data) {
        if (index <= 0) {
            Node node = new Node(data);
            node.next = head;
            if (head != null) head.prev = node;
            head = node;
            return;
        }
        Node temp = head;
        for (int i = 0; i < index - 1 && temp != null; i++) {
            temp = temp.next;
        }
        if (temp == null) {
            insert(data);
            return;
        }
        Node node = new Node(data);
        node.next = temp.next;
        node.prev = temp;
        if (temp.next != null) temp.next.prev = node;
        temp.next = node;
    }

    public void insertFirst(AutomationAction data) {
        insertAt(0, data);
    }

    public void insertAfter(AutomationAction target, AutomationAction newAction) {
        int index = indexOf(target);
        if (index != -1) {
            insertAt(index + 1, newAction);
        } else {
            insert(newAction);
        }
    }

    public boolean deleteNode(AutomationAction data) {
        Node temp = head;
        while (temp != null) {
            if (temp.data == data) {
                if (temp.prev != null) temp.prev.next = temp.next;
                else head = temp.next;
                
                if (temp.next != null) temp.next.prev = temp.prev;
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    public int indexOf(AutomationAction data) {
        Node temp = head;
        int index = 0;
        while (temp != null) {
            if (temp.data == data) return index;
            temp = temp.next;
            index++;
        }
        return -1;
    }
    
    public AutomationAction getAt(int index) {
        Node temp = head;
        for (int i = 0; i < index && temp != null; i++) {
            temp = temp.next;
        }
        return temp != null ? temp.data : null;
    }

    public int size() {
        int count = 0;
        Node temp = head;
        while (temp != null) {
            count++;
            temp = temp.next;
        }
        return count;
    }

    public List<AutomationAction> forwardTraversal() {
        List<AutomationAction> list = new ArrayList<>();
        Node temp = head;
        while (temp != null) {
            list.add(temp.data);
            temp = temp.next;
        }
        return list;
    }

    public AutomationAction getHead() {
        return head != null ? head.data : null;
    }

    public boolean moveUp(AutomationAction data) {
        Node temp = head;
        while (temp != null && temp.data != data) temp = temp.next;
        if (temp == null || temp.prev == null) return false;
        
        AutomationAction swap = temp.data;
        temp.data = temp.prev.data;
        temp.prev.data = swap;
        return true;
    }

    public boolean moveDown(AutomationAction data) {
        Node temp = head;
        while (temp != null && temp.data != data) temp = temp.next;
        if (temp == null || temp.next == null) return false;
        
        AutomationAction swap = temp.data;
        temp.data = temp.next.data;
        temp.next.data = swap;
        return true;
    }
}
package ua.edu.sumdu.j2se.rozghon.tasks.model;

public class LinkedTaskList extends AbstractTaskList {
    private Node first; //the first node in the list
    private Node last; //the last node in the list

    private static class Node {
        private Task task; //element of the list
        private Node next; //next node in the list
        private Node prev; //previous node in the list

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    public void add(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Can't add empty pointer to list.");
        }
        Node node;
        if (first == null) {
            /* if list is empty, create node
            that don't has previous and next nodes */
            node = new Node(null, task, null);
            //created node become the first and the last node in the list
            first = node;
            last = node;
        } else {
            //create node that has link on it's previous element
            node = new Node(last, task, null);
            last.next = node; //previous node get link on created one
            last = node; //created node become the last node in the list
        }
        size++; //increase size of list
    }

    public boolean remove(Task task) {
        if (size == 0) { //can't remove anything, if list is empty
            return false;
        } else if (size == 1 && first.task.equals(task)) {
            //removal of node from list that has only one node
            first = null;
            last = null;
            size--;
            return true;
        } else if (size == 1 && !first.task.equals(task)) {
            return false;
        } else if (first.task.equals(task)) {
            //removal of the first node from list
            //the second node don't have previous one anymore
            first.next.prev = null;
            first = first.next; //the second node become the first
            size--;
            return true;
        } else if (last.task.equals(task)) {
            //removal of the last node from list
            //the penultimate node don't have next one anymore
            last.prev.next = null;
            last = last.prev; //the penultimate node become the last
            size--;
            return true;
        } else {
            //removal of node from the middle of list
            Node node = first.next;
            while (node != last) {
                //compare each task from the list with the set task
                if (node.task.equals(task)) {
                    //previous node get link on the next one
                    node.prev.next = node.next;
                    //next node get link on the previous one
                    node.next.prev = node.prev;
                    size--;
                    return true;
                }
                node = node.next; //pass to the next node
            }
            return false;
        }
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        if (index >= size) {
            throw new IndexOutOfBoundsException("List includes " + size
                    + " tasks. There is not " + index
                    + "th task in this list.");
        }
        Node node = first;
        for (int i = 0; i < index; i++) {
            node = node.next; //pass to the desired node
        }
        return node.task; //return found task
    }

    @Override
    public Object clone() {
        try {
            LinkedTaskList taskList = (LinkedTaskList) super.clone();
            taskList.size = 0;
            taskList.first = null;
            taskList.last = null;
            for (LinkedTaskList.Node i = first; i != null; i = i.next) {
                taskList.add(i.task);
            }
            return taskList;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

package ua.edu.sumdu.j2se.rozghon.tasks;

public abstract class AbstractTaskList {
    protected int size; //amount of elements in the list
    public int size() {
        return size;
    }
    public abstract void add(Task task);
    public abstract boolean remove(Task task);
    public abstract Task getTask(int index);
    public abstract AbstractTaskList incoming(int from, int to);
}

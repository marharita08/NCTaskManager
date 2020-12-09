package ua.edu.sumdu.j2se.rozghon.tasks;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ArrayTaskList extends AbstractTaskList {
    private final int DEFAULT_SIZE = 10; //setting the default size of the array
    private Task[] task;

    public ArrayTaskList() {
        //allocating memory for an array of ten elements
        task = new Task[DEFAULT_SIZE];
    }

    public void add(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Can't add empty pointer to list.");
        }
        if (size == this.task.length) {
            /* if the allocated memory is not enough to add new task
            * allocate 1.5 times more memory and copy values of the array*/
            this.task = Arrays.copyOf(this.task,
                    (int) (this.task.length * 1.5));
        }
        //add new task to the array and increase index by one
        this.task[size++] = task;
    }

    public boolean remove(Task task) {
        for (int i = 0; i < size; i++) {
            //compare each task from the array with the set task
            if (this.task[i].equals(task)) {
                for (int j = i; j < size - 1; j++) {
                    /* all tasks, that follows after found task,
                    * shift by one cell left */
                    this.task[j] = this.task[j + 1];
                }
                this.task[--size] = null; //reset the last task of the array
                return true;
            }
        }
        return false; //if the array don't include the set task
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        if (index >= size) {
            throw new IndexOutOfBoundsException("List includes " + size
                    + " tasks. There is not " + index
                    + "th task in this list.");
        }
        return task[index];
    }

    @Override
    public Object clone() {
        try {
            ArrayTaskList taskList = (ArrayTaskList) super.clone();
            taskList.task = Arrays.copyOf(this.task, this.size);
            taskList.size = this.size;
            return taskList;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public Stream<Task> getStream(){
        Iterable<Task> iterable = () -> this.iterator();
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}

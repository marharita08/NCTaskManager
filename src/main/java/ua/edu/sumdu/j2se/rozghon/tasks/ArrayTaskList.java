package ua.edu.sumdu.j2se.rozghon.tasks;
import java.util.Arrays;

public class ArrayTaskList {
    private final int DEFAULT_SIZE = 10; //setting the default size of the array
    private Task[] task;
    private int index; //amount of tasks in the array

    public ArrayTaskList() {
        //allocating memory for an array of ten elements
        task = new Task[DEFAULT_SIZE];
    }

    public void add(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Can't add empty pointer to list.");
        }
        if (index == this.task.length) {
            /* if the allocated memory is not enough to add new task
            * allocate 1.5 times more memory and copy values of the array*/
            this.task = Arrays.copyOf(this.task,
                    (int) (this.task.length * 1.5));
        }
        //add new task to the array and increase index by one
        this.task[index++] = task;
    }

    public boolean remove(Task task) {
        for (int i = 0; i < index; i++) {
            //compare each task from the array with the set task
            if (this.task[i].equals(task)) {
                for (int j = i; j < index - 1; j++) {
                    /* all tasks, that follows after found task,
                    * shift by one cell left */
                    this.task[j] = this.task[j + 1];
                }
                this.task[--index] = null; //reset the last task of the array
                return true;
            }
        }
        return false; //if the array don't include the set task
    }

    public int size() {
        return index;
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        if (index >= this.index) {
            throw new IndexOutOfBoundsException("List includes " + this.index
                    + " tasks. There is not " + index
                    + "th task in this list.");
        }
        return task[index];
    }

    public ArrayTaskList incoming(int from,
                                  int to) throws IllegalArgumentException {
        if (from >= to) {
            throw new IllegalArgumentException(
                    "Parameter from must be less than parameter to");
        }
        ArrayTaskList taskList = new ArrayTaskList(); //create taskList
        for (int i = 0; i < index; i++) {
            /* check whether the task will be executed
            * in the set interval of time */
            if (task[i].nextTimeAfter(from) > from
                    && task[i].nextTimeAfter(from) < to) {
                taskList.add(task[i]); //add current task to taskList
            }
        }
        return taskList;
    }
}

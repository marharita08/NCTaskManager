package ua.edu.sumdu.j2se.rozghon.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractTaskList implements Iterable, Cloneable {
    protected int size; //amount of elements in the list

    public int size() {
        return size;
    }

    public final AbstractTaskList incoming(int from,
                                  int to) throws IllegalArgumentException {
        if (from >= to) {
            throw new IllegalArgumentException(
                    "Parameter from must be less than parameter to");
        }
        List<Task> list =
                this.getStream().filter((p) -> p.nextTimeAfter(from) > from
                && p.nextTimeAfter(from) < to).collect(Collectors.toList());
        AbstractTaskList taskList;
        //create taskList
        if (this.getClass() == ArrayTaskList.class) {
            taskList = TaskListFactory.createTaskList(ListTypes.types.ARRAY);
        } else {
            taskList = TaskListFactory.createTaskList(ListTypes.types.LINKED);
        }
        for (Task task:list) {
            taskList.add(task);
        }
        return taskList;
    }

    public abstract void add(Task task);
    public abstract boolean remove(Task task);
    public abstract Task getTask(int index);
    public abstract Stream<Task> getStream();

    @Override
    public Iterator<Task> iterator() {
        return new Iterator<>() {
            private int next;
            private int lastRet = -1;

            @Override
            public boolean hasNext() {
                return next != size;
            }

            @Override
            public Task next() {
                lastRet = next;
                return getTask(next++);
            }

            @Override
            public void remove() {
                if (lastRet < 0) {
                    throw new IllegalStateException();
                }
                AbstractTaskList.this.remove(getTask(lastRet));
                next = lastRet;
                lastRet = -1;
            }
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractTaskList)) {
            return false;
        }
        boolean equal = size == ((AbstractTaskList) obj).size;
        if (equal && size == 0) {
            return true;
        }
        int i = 0;
        while (equal) {
            equal = getTask(i).equals(((AbstractTaskList) obj).getTask(i));
            i++;
            if (i == size) {
                break;
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        final int PRIME = 27;
        int result = 1;
        for (int i = 0; i < size; i++) {
            result = result * PRIME + getTask(i).hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        String str = "\n" +  getClass().getName() + "\n"
                + "Hash Code: " + hashCode() + "\n{";
        for (int i = 0; i < size; i++) {
            str = str + getTask(i).toString();
        }
        str = str + "}";
        return str;
    }
}

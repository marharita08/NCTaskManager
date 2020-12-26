package ua.edu.sumdu.j2se.rozghon.tasks;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractTaskList implements Iterable, Cloneable {
    protected int size; //amount of elements in the list

    public int size() {
        return size;
    }

    public final AbstractTaskList incoming(LocalDateTime from,
                                           LocalDateTime to)
            throws IllegalArgumentException {
        if (from.isAfter(to) || from.isEqual(to)) {
            throw new IllegalArgumentException(
                    "'From' must be before 'to'");
        }
        AbstractTaskList taskList;
        //create taskList
        if (this.getClass() == ArrayTaskList.class) {
            taskList = TaskListFactory.createTaskList(ListTypes.types.ARRAY);
        } else {
            taskList = TaskListFactory.createTaskList(ListTypes.types.LINKED);
        }
        this.getStream().filter(p -> p.nextTimeAfter(from) != null
                && (p.nextTimeAfter(from).isBefore(to)
                || p.nextTimeAfter(from).isEqual(to))).forEach(taskList::add);
        return taskList;
    }

    public Stream<Task> getStream(){
        Iterable<Task> iterable = () -> this.iterator();
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public abstract void add(Task task);
    public abstract boolean remove(Task task);
    public abstract Task getTask(int index);

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

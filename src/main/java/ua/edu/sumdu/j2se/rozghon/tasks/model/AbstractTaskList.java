package ua.edu.sumdu.j2se.rozghon.tasks.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractTaskList
        implements Iterable, Cloneable, Serializable {
    protected int size; //amount of elements in the list

    /**
     * Method for getting size of list.
     * @return size of list
     */
    public int size() {
        return size;
    }

    /**
     * Method finds tasks that time between two dates.
     * @param from first date
     * @param to second date
     * @return list of tasks that time between first and second dates
     * @throws IllegalArgumentException if first date
     *                                  is after or equals to the second one
     */
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

    /**
     * Method transforms task list into stream.
     * @return stream
     */
    public Stream<Task> getStream() {
        Iterable<Task> iterable = () -> this.iterator();
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Method for adding task to list.
     * @param task task that adding to list
     */
    public abstract void add(Task task);

    /**
     * Method for removing task from the list.
     * @param task task that removing from the list
     * @return true if removing finish successfully
     */
    public abstract boolean remove(Task task);

    /**
     * Method for getting task by index.
     * @param index task index
     * @return task
     */
    public abstract Task getTask(int index);

    /**
     * Returns an iterator over tasks.
     * @return an Iterator
     */
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

    /**
     * Method for comparison of two task lists.
     * @param obj second task list
     * @return tru if current task list is equal to second task list
     */
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

    /**
     * Method for getting hash code of task list.
     * @return hash code
     */
    @Override
    public int hashCode() {
        final int PRIME = 27;
        int result = 1;
        for (int i = 0; i < size; i++) {
            result = result * PRIME + getTask(i).hashCode();
        }
        return result;
    }

    /**
     * Method transforms task list into string.
     * @return text representation of task list
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        stringBuilder.append(getClass().getName());
        stringBuilder.append("\n");
        stringBuilder.append("Hash code: ");
        stringBuilder.append(hashCode());
        stringBuilder.append("\n{");
        for (int i = 0; i < size; i++) {
            stringBuilder.append(getTask(i).toString());
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}

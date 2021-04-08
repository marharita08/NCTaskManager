package ua.edu.sumdu.j2se.rozghon.tasks.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Tasks {
    /**
     * Method finds tasks in got task list that time between two dates.
     * @param tasks task list
     * @param from first date
     * @param to second date
     * @return list of tasks that time between first and second dates
     * @throws IllegalArgumentException if task list is not initialized
     *                 or if first date is after or equal to the second one
     */
    public static Iterable<Task> incoming(Iterable<Task> tasks,
                                          LocalDateTime from,
                                          LocalDateTime to)
            throws IllegalArgumentException {
        if (from.isAfter(to) || from.isEqual(to)) {
            throw new IllegalArgumentException(
                    "'From' must be before 'to'");
        }
        if (tasks == null) {
            throw new IllegalArgumentException(
                    "tasks should be not null");
        }
        //create taskList
        AbstractTaskList taskList =
                TaskListFactory.createTaskList(ListTypes.types.ARRAY);
        for (Task task:tasks) { //for every task from the set list
            if (task.nextTimeAfter(from) != null
            && (task.nextTimeAfter(from).isBefore(to)
            || task.nextTimeAfter(from).isEqual(to))) {
                //add task to taskList
                // if it will be doing in the middle of set interval
                taskList.add(task);
            }
        }
        return taskList;
    }

    /**
     * Method forms calendar of tasks
     * from got task list that time between two dates.
     * @param tasks task list
     * @param start first date
     * @param end second date
     * @return map that include task time and set of tasks
     */
    public static SortedMap<LocalDateTime,
            Set<Task>> calendar(Iterable<Task> tasks,
                                LocalDateTime start,
                                LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException(
                    "start must be before end");
        }
        if (tasks == null) {
            throw new IllegalArgumentException(
                    "tasks should be not null");
        }
        SortedMap<LocalDateTime, Set<Task>> map = new TreeMap<>(); //create map
        for (Task task:tasks) { //for every task from the set list
            //searching for time from set interval when task will be doing
            for (LocalDateTime i = task.nextTimeAfter(start);
                 i != null && (i.isBefore(end) || i.isEqual(end));
                 i = task.nextTimeAfter(i)) {
                if (!map.containsKey(i)) {
                    //if map not includes tasks with found doing time
                    Set<Task> set = new HashSet<>(); //create new set
                    set.add(task); //add current task to created set
                    map.put(i, set); //add time and set to map
                } else {
                    //if map already includes tasks with found doing time
                    //add task to already existed set into map
                    map.get(i).add(task);
                }

            }
        }
        return map;
    }
}

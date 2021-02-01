package ua.edu.sumdu.j2se.rozghon.tasks.model;

public class TaskListFactory {
    /**
     * Method creates object of ArrayTaskList or LinkedTaskList.
     * according to got type
     * @param type type of task list
     * @return task list
     * @throws NullPointerException if got type is null
     */
    public static AbstractTaskList createTaskList(ListTypes.types type)
            throws NullPointerException {
        if (type == null) {
            throw new NullPointerException("Type should be "
                    + ListTypes.types.ARRAY + " or "
                    + ListTypes.types.LINKED + ", not null");
        }
        AbstractTaskList taskList = null;
        switch (type) {
            case ARRAY:
                taskList = new ArrayTaskList();
                break;
            case LINKED:
                taskList = new LinkedTaskList();
                break;
        }
        return taskList;
    }
}

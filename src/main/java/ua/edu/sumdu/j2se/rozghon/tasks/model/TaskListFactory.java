package ua.edu.sumdu.j2se.rozghon.tasks.model;

public class TaskListFactory {
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

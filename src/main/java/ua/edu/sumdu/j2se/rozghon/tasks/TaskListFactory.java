package ua.edu.sumdu.j2se.rozghon.tasks;

public class TaskListFactory {
    public static AbstractTaskList createTaskList(ListTypes.types type)
            throws NullPointerException {
        if (type == null) {
            throw new NullPointerException("Type should be "
                    + ListTypes.types.ARRAY + " or "
                    + ListTypes.types.LINKED + ", not null");
        }
        switch (type) {
            case ARRAY:
                return new ArrayTaskList();
            case LINKED:
                return new LinkedTaskList();
            default:
                return null;
        }
    }
}

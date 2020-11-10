package ua.edu.sumdu.j2se.rozghon.tasks;

public class Task {
    private String title;
    private int time;
    private int start;
    private int end;
    private int interval;
    private boolean active;
    private boolean repeated;

    /**
     * Constructor that creates non repetitive task
     * @param title name of task
     * @param time time when task will be done,
     *             it should be a positive number
     */
    public Task(String title, int time) throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException(
                    "Time should be a positive number.");
        }
        this.title = title;
        this.time = time;
    }

    /**
     * Constructor that creates repetitive task
     * @param title name of task
     * @param start time when task will start,
     *              it should be a positive number,
     *              that should be less than parameter end
     * @param end time when task will end,
     *            it should be a positive number,
     *            that should be more than parameter start
     * @param interval interval between doing task,
     *                 it should be a positive number,
     *                 that should be less than difference
     *                 between parameters start and end
     */
    public Task(String title, int start, int end,
                int interval) throws IllegalArgumentException {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException(
                    "Start and end should be positive numbers.");
        }
        if (start >= end) {
            throw new IllegalArgumentException(
                    "Start should be less than end.");
        }
        if (interval <= 0 || interval >= end - start) {
            throw new IllegalArgumentException("Interval should more than zero "
                    + "and less than difference between end and start.");
        }
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
        repeated = true;
    }

    /**
     * Method for getting name of the task
     * @return name of the task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method for changing name of the task
     * @param title name of the task
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Method for recognizing state of the task
     * @return true, if the task is active;
     * false, if the task is nonactive
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Method for changing state of the task
     * @param active state of the task
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Method for recognizing time when task will be done
     * @return time when task will be done, if task is non repetitive;
     * time when task will start, if task is repetitive
     */
    public int getTime() {
        if (repeated) {
            return start;
        } else {
            return time;
        }
    }

    /**
     * Method for setting time when task will be done
     * If task was repetitive, it will become non repetitive
     * @param time time when task will be done,
     *             it should be a positive number
     */
    public void setTime(int time) throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException(
                    "Time should be a positive number.");
        }
        this.time = time;
        if (repeated) {
            repeated = false;
            start = 0;
            end = 0;
            interval = 0;
        }
    }

    /**
     * Method for recognizing time when task will start
     * @return time when task will start, if task is repetitive;
     * time when task will be done, if task is non repetitive
     */
    public int getStartTime() {
        if (repeated) {
            return start;
        } else {
            return time;
        }
    }
    /**
     * Method for recognizing time when task will end
     * @return time when task will end, if task is repetitive;
     * time when task will be done, if task is non repetitive
     */
    public int getEndTime() {
        if (repeated) {
            return end;
        } else {
            return time;
        }
    }

    /**
     *  Method for recognizing interval between doing task
     * @return interval between doing task
     */
    public int getRepeatInterval() {
        return interval;
    }

    /**
     * Method for setting time
     * If task was non repetitive, it will become repetitive
     *  @param start time when task will start,
     *               it should be a positive number,
     *               that should be less than parameter end
     *  @param end time when task will end,
     *             it should be a positive number,
     *             that should be more than parameter start
     *  @param interval interval between doing task,
     *                  it should be a positive number,
     *                  that should be less than difference
     *                  between parameters start and end
     */
    public void setTime(int start, int end,
                        int interval) throws IllegalArgumentException {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException(
                    "Start and end should be positive numbers.");
        }
        if (start >= end) {
            throw new IllegalArgumentException(
                    "Start should be less than end.");
        }
        if (interval <= 0 || interval >= end - start) {
            throw new IllegalArgumentException("Interval should more than zero "
                    + "and less than difference between start and end.");
        }
        this.start = start;
        this.end = end;
        this.interval = interval;
        if (!repeated) {
            time = 0;
            repeated = true;
        }
    }

    /**
     * Method for recognizing repetitive of the task
     * @return true, if task is repetitive; false, if task is non repetitive
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * Method for defining next time of doing task
     * @param current current time
     * @return next time of doing task;
     * -1, if task is nonactive or if task is already done
     */
    public int nextTimeAfter(int current) {
        if (active && repeated
                && current < ((end - start) / interval) * interval + start) {
            if (current < start) {
                return start;
            } else {
                return (((current - start) / interval) + 1) * interval + start;
            }
        } else if (active && !repeated && current < time) {
            return time;
        } else {
            return -1;
        }
    }
}

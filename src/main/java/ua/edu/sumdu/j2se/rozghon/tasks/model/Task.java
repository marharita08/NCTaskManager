package ua.edu.sumdu.j2se.rozghon.tasks.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task implements Cloneable, Serializable {
    private String title;
    private LocalDateTime time;
    private LocalDateTime start;
    private LocalDateTime end;
    private int interval;
    private boolean active;
    private boolean repeated;

    public Task() { }
    /**
     * Constructor that creates non repetitive task
     * @param title name of task
     * @param time time when task will be done,
     *             it should be a positive number
     */
    public Task(String title, LocalDateTime time)
            throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException(
                    "Time should be not null.");
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
    public Task(String title, LocalDateTime start, LocalDateTime end,
                int interval) throws IllegalArgumentException {
        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "Start and end should be not null.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Start should be before end.");
        }
        Duration duration = Duration.between(start, end);
        int diff = (int)Math.abs(duration.toSeconds());
        if (interval <= 0 || interval >= diff) {
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
    public LocalDateTime getTime() {
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
    public void setTime(LocalDateTime time) throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException(
                    "Time should be not null.");
        }
        this.time = time;
        if (repeated) {
            repeated = false;
            start = null;
            end = null;
            interval = 0;
        }
    }

    /**
     * Method for recognizing time when task will start
     * @return time when task will start, if task is repetitive;
     * time when task will be done, if task is non repetitive
     */
    public LocalDateTime getStartTime() {
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
    public LocalDateTime getEndTime() {
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
    public void setTime(LocalDateTime start, LocalDateTime end,
                        int interval) throws IllegalArgumentException {
        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "Start and end should be not null.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Start should be before end.");
        }
        this.start = start;
        this.end = end;
        this.interval = interval;
        if (!repeated) {
            time = null;
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
    public LocalDateTime nextTimeAfter(LocalDateTime current) {
        if (active && repeated) {
            Duration duration = Duration.between(start, end);
            long diff = Math.abs(duration.toSeconds());
            if (current.isBefore(start.plusSeconds(
                    (diff / interval) * interval))) {
                if (current.isBefore(start)) {
                    return start;
                } else {
                    if (start.equals(current)) {
                        return start.plusSeconds(interval);
                    }
                    duration = Duration.between(start, current);
                    diff = Math.abs(duration.toSeconds());
                    return start.plusSeconds(((
                            diff / interval) + 1) * interval);
                }
            }
        } else if (active && !repeated && current.isBefore(time)) {
            return time;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.title.equals(((Task) obj).title)
                && (this.time == null ? this.time == ((Task) obj).time
                : this.time.isEqual(((Task) obj).time))
                && (this.start == null ? this.start == ((Task) obj).start
                : this.start.isEqual(((Task) obj).start))
                && (this.end == null ? this.end == ((Task) obj).end
                : this.end.isEqual(((Task) obj).end))
                && this.interval == ((Task) obj).interval
                && this.active == ((Task) obj).active
                && this.repeated == ((Task) obj).repeated;
    }

    @Override
    public int hashCode() {
        final int PRIME = 23;
        int result = 1;
        result = result * PRIME + title.hashCode();
        if (!repeated) {
            result = result * PRIME + time.hashCode();
        }
        if (repeated) {
            result = result * PRIME + start.hashCode();
            result = result * PRIME + end.hashCode();
        }
        result = result * PRIME + interval;
        result = result * PRIME + (active ? 1 : 0);
        result = result * PRIME + (repeated ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "\n" + getClass().getName() + "\n"
                + "Hash Code: " + hashCode() + "\n\n"
                + "Title: " + title + "\n"
                + "Time: " + time + "\n"
                + "Start: " + start + "\n"
                + "End: " + end + "\n"
                + "Interval: " + interval + "\n"
                + "Active: " + active + "\n"
                + "Repeated: " + repeated + "\n";
    }

    @Override
    public Object clone() {
        try {
            Task task = (Task) super.clone();
            task.title = this.title;
            task.time = this.time;
            task.start = this.start;
            task.end = this.end;
            task.interval = this.interval;
            task.active = this.active;
            task.repeated = this.repeated;
            return task;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

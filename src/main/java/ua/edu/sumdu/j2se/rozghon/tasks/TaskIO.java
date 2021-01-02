package ua.edu.sumdu.j2se.rozghon.tasks;

import com.google.gson.Gson;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TaskIO {
    public static void write(AbstractTaskList tasks, OutputStream out) {
        try (DataOutputStream output = new DataOutputStream(out)) {
            output.writeInt(tasks.size());
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.getTask(i);
                output.writeInt(task.getTitle().length());
                output.writeUTF(task.getTitle());
                output.writeInt(task.isActive() ? 1 : 0);
                output.writeInt(task.getRepeatInterval());
                output.writeLong(task.getTime().toEpochSecond(ZoneOffset.UTC));
                if (task.isRepeated()) {
                    output.writeLong(task.getEndTime().toEpochSecond(ZoneOffset.UTC));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read(AbstractTaskList tasks, InputStream in) {
        try (DataInputStream input = new DataInputStream(in)) {
            int size = input.readInt();
            for (int i = 0; i < size; i++) {
                Task task = new Task();
                int length = input.readInt();
                task.setTitle(input.readUTF());
                task.setActive(input.readInt() == 1);
                int interval = input.readInt();
                if (interval == 0) {
                    task.setTime(LocalDateTime.ofEpochSecond(
                            input.readLong(), 0, ZoneOffset.UTC));
                } else {
                    task.setTime(LocalDateTime.ofEpochSecond(
                            input.readLong(), 0, ZoneOffset.UTC),
                            LocalDateTime.ofEpochSecond(
                                    input.readLong(), 0, ZoneOffset.UTC),
                            interval);
                }
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBinary(AbstractTaskList tasks, File file) {
        try (FileOutputStream output = new FileOutputStream(file)) {
            write(tasks, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readBinary(AbstractTaskList tasks, File file) {
        try (FileInputStream input = new FileInputStream(file)) {
            read(tasks, input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(AbstractTaskList tasks, Writer out) {
        Gson gson = new Gson();
        try (BufferedWriter output = new BufferedWriter(out)) {
            output.write(gson.toJson(tasks));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read(AbstractTaskList tasks, Reader in) {
        try (BufferedReader input = new BufferedReader(in)) {
            String text;
            Gson gson = new Gson();
            while ((text = input.readLine()) != null) {
                AbstractTaskList taskList = gson.fromJson(text,
                        tasks.getClass());
                for (Object task : taskList) {
                    tasks.add((Task) task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeText(AbstractTaskList tasks, File file) {
        try (FileWriter output = new FileWriter(file)) {
            write(tasks, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readText(AbstractTaskList tasks, File file) {
        try (FileReader input = new FileReader(file)) {
            read(tasks, input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

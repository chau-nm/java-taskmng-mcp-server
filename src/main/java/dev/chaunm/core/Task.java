package dev.chaunm.core;

public class Task {
    private static long INCREASEMENT_ID = 0;

    private long id;
    private String name;
    private TaskStatus status;

    public Task(String name) {
        this(name, TaskStatus.TODO);
    }

    public Task(String name, TaskStatus status) {
        this.id = getNextId();
        this.name = name;
        this.status = status;
    }

    private long getNextId() {
        return ++INCREASEMENT_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}

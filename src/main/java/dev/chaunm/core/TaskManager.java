package dev.chaunm.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskManager {
    // Wrapped in ArrayList because List.of is immutable and add/remove would throw
    private final List<Task> tasks = new ArrayList<>(List.of(
            new Task("Set up the project repository", TaskStatus.DONE),
            new Task("Write the MCP server with the Java SDK", TaskStatus.DOING),
            new Task("Add the find_all_task tool", TaskStatus.DOING),
            new Task("Write unit tests for TaskManager"),
            new Task("Connect the server to Claude Code"),
            new Task("Write the README", TaskStatus.PENDING),
            new Task("Support the 2026-07-28 protocol", TaskStatus.CANCEL)
    ));

    public TaskManager() {}

    public int getSize() {
        return tasks.size();
    }

    public List<Task> findAll() {
        return tasks;
    }

    public Optional<Task> findById(long id) {
        return tasks.stream().filter(task -> task.getId() == id).findFirst();
    }

    public List<Task> filter(Long id, String name, TaskStatus status) {
        return tasks.stream()
                .filter(task -> id == null || id.equals(task.getId()))
                .filter(task -> name == null || task.getName().contains(name))
                .filter(task -> status == null || task.getStatus() == status)
                .toList();
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public void update(long id, String name, TaskStatus status) {
         tasks.stream()
            .filter(task -> task.getId() == id)
            .findFirst()
            .ifPresent(task -> {
                task.setName(name);
                task.setStatus(status);
            });
    }

    public void remove(long id) {
        tasks.removeIf(task -> task.getId() == id);
    }
}

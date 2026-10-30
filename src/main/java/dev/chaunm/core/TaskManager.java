package dev.chaunm.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();

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

package dev.chaunm.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new TaskManager();
    }

    private Task addTask(String name) {
        Task task = new Task(name);
        taskManager.add(task);
        return task;
    }

    private Task addTask(String name, TaskStatus status) {
        Task task = addTask(name);
        task.setStatus(status);
        return task;
    }

    @Test
    public void new_manager_is_empty() {
        assertEquals(0, taskManager.getSize());
        assertTrue(taskManager.findAll().isEmpty());
    }

    @Test
    public void add_task_success() {
        taskManager.add(new Task("TASK"));
        assertEquals(1, taskManager.getSize());
    }

    @Test
    public void add_multiple_tasks_keeps_insertion_order() {
        Task t1 = addTask("TASK 1");
        Task t2 = addTask("TASK 2");
        Task t3 = addTask("TASK 3");

        assertEquals(3, taskManager.getSize());
        assertEquals(List.of(t1, t2, t3), taskManager.findAll());
    }

    @Test
    public void new_task_has_default_status_todo_and_unique_id() {
        Task t1 = addTask("TASK 1");
        Task t2 = addTask("TASK 2");

        assertEquals(TaskStatus.TODO, t1.getStatus());
        assertNotEquals(t1.getId(), t2.getId());
    }

    @Test
    public void find_by_id_returns_task_when_exists() {
        addTask("TASK 1");
        Task t2 = addTask("TASK 2");

        Optional<Task> result = taskManager.findById(t2.getId());

        assertTrue(result.isPresent());
        assertSame(t2, result.get());
    }

    @Test
    public void find_by_id_returns_empty_when_not_exists() {
        Task t1 = addTask("TASK 1");

        assertTrue(taskManager.findById(t1.getId() + 1000).isEmpty());
    }

    @Test
    public void find_by_id_on_empty_manager_returns_empty() {
        assertTrue(taskManager.findById(1).isEmpty());
    }

    @Test
    public void filter_with_all_null_returns_all_tasks() {
        Task t1 = addTask("TASK 1");
        Task t2 = addTask("TASK 2");

        assertEquals(List.of(t1, t2), taskManager.filter(null, null, null));
    }

    @Test
    public void filter_by_id() {
        addTask("TASK 1");
        Task t2 = addTask("TASK 2");

        assertEquals(List.of(t2), taskManager.filter(t2.getId(), null, null));
    }

    @Test
    public void filter_by_name_matches_substring() {
        Task t1 = addTask("Write report");
        addTask("Fix bug");
        Task t3 = addTask("Review report");

        assertEquals(List.of(t1, t3), taskManager.filter(null, "report", null));
    }

    @Test
    public void filter_by_name_is_case_sensitive() {
        addTask("Write report");

        assertTrue(taskManager.filter(null, "REPORT", null).isEmpty());
    }

    @Test
    public void filter_by_status() {
        addTask("TASK 1", TaskStatus.TODO);
        Task t2 = addTask("TASK 2", TaskStatus.DONE);
        Task t3 = addTask("TASK 3", TaskStatus.DONE);

        assertEquals(List.of(t2, t3), taskManager.filter(null, null, TaskStatus.DONE));
    }

    @Test
    public void filter_by_name_and_status_combined() {
        addTask("Write report", TaskStatus.TODO);
        Task t2 = addTask("Review report", TaskStatus.DOING);
        addTask("Fix bug", TaskStatus.DOING);

        assertEquals(List.of(t2), taskManager.filter(null, "report", TaskStatus.DOING));
    }

    @Test
    public void filter_with_all_criteria_returns_empty_when_one_does_not_match() {
        Task t1 = addTask("Write report", TaskStatus.TODO);

        assertTrue(taskManager.filter(t1.getId(), "report", TaskStatus.DONE).isEmpty());
    }

    @Test
    public void filter_returns_empty_when_no_match() {
        addTask("TASK 1");

        assertTrue(taskManager.filter(null, "not-exist", null).isEmpty());
    }

    @Test
    public void update_changes_name_and_status() {
        Task t1 = addTask("TASK 1");

        taskManager.update(t1.getId(), "TASK 1 updated", TaskStatus.DONE);

        Task updated = taskManager.findById(t1.getId()).orElseThrow();
        assertEquals("TASK 1 updated", updated.getName());
        assertEquals(TaskStatus.DONE, updated.getStatus());
    }

    @Test
    public void update_only_affects_target_task() {
        Task t1 = addTask("TASK 1");
        Task t2 = addTask("TASK 2");

        taskManager.update(t1.getId(), "changed", TaskStatus.CANCEL);

        assertEquals("TASK 2", t2.getName());
        assertEquals(TaskStatus.TODO, t2.getStatus());
    }

    @Test
    public void update_non_existing_id_does_nothing() {
        Task t1 = addTask("TASK 1");

        assertDoesNotThrow(() -> taskManager.update(t1.getId() + 1000, "changed", TaskStatus.DONE));
        assertEquals("TASK 1", t1.getName());
        assertEquals(TaskStatus.TODO, t1.getStatus());
        assertEquals(1, taskManager.getSize());
    }

    @Test
    public void remove_existing_task() {
        Task t1 = addTask("TASK 1");
        Task t2 = addTask("TASK 2");

        taskManager.remove(t1.getId());

        assertEquals(1, taskManager.getSize());
        assertTrue(taskManager.findById(t1.getId()).isEmpty());
        assertTrue(taskManager.findById(t2.getId()).isPresent());
    }

    @Test
    public void remove_non_existing_id_does_nothing() {
        addTask("TASK 1");

        taskManager.remove(-1);

        assertEquals(1, taskManager.getSize());
    }

    @Test
    public void remove_on_empty_manager_does_nothing() {
        assertDoesNotThrow(() -> taskManager.remove(1));
        assertEquals(0, taskManager.getSize());
    }
}

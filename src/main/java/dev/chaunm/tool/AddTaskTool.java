package dev.chaunm.tool;

import dev.chaunm.core.Task;
import dev.chaunm.core.TaskManager;
import dev.chaunm.core.TaskStatus;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class AddTaskTool extends TaskTool {

    public AddTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("add_task", schema(props(
                        "name", nameProperty("Task name"),
                        "status", statusProperty()), "name"))
                .description("Create a task and return it with its new id. Status defaults to TODO.")
                .annotations(annotations(false, false, false))
                .build();
    }

    @Override
    protected Object execute(Map<String, Object> args) {
        String name = requiredString(args, "name");
        TaskStatus status = optionalStatus(args);
        Task task = new Task(name, status != null ? status : TaskStatus.TODO);
        taskManager.add(task);
        return task;
    }
}

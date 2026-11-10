package dev.chaunm.tool;

import dev.chaunm.core.Task;
import dev.chaunm.core.TaskManager;
import dev.chaunm.core.TaskStatus;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class UpdateTaskTool extends TaskTool {

    public UpdateTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("update_task", schema(props(
                        "id", idProperty(),
                        "name", nameProperty("New task name"),
                        "status", statusProperty()), "id"))
                .description("Change the name and/or status of a task and return the updated task. "
                        + "Fields left out keep their current value.")
                .annotations(annotations(false, false, true))
                .build();
    }

    @Override
    protected Object execute(Map<String, Object> args) {
        Task task = findTask(requiredId(args));
        String name = optionalString(args, "name");
        TaskStatus status = optionalStatus(args);
        if (name == null && status == null) {
            throw new ToolException("Give name and/or status to update.");
        }
        // TaskManager.update overwrites both fields, so pass the current value for the one left out
        taskManager.update(task.getId(),
                name != null ? name : task.getName(),
                status != null ? status : task.getStatus());
        return task;
    }
}

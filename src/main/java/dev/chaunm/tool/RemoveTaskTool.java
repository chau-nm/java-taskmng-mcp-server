package dev.chaunm.tool;

import dev.chaunm.core.Task;
import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class RemoveTaskTool extends TaskTool {

    public RemoveTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("remove_task", schema(props("id", idProperty()), "id"))
                .description("Delete a task by its id and return the deleted task")
                .annotations(annotations(false, true, true))
                .build();
    }

    @Override
    protected Object execute(Map<String, Object> args) {
        // Look it up first: TaskManager.remove does nothing for an unknown id
        Task task = findTask(requiredId(args));
        taskManager.remove(task.getId());
        return task;
    }
}

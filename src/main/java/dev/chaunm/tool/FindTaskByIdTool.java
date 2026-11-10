package dev.chaunm.tool;

import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class FindTaskByIdTool extends TaskTool {

    public FindTaskByIdTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("find_task_by_id", schema(props("id", idProperty()), "id"))
                .description("Get one task by its id")
                .annotations(annotations(true, false, true))
                .build();
    }

    @Override
    protected Object execute(Map<String, Object> args) {
        return findTask(requiredId(args));
    }
}

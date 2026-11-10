package dev.chaunm.tool;

import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class FindAllTaskTool extends TaskTool {

    public FindAllTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("find_all_task", schema(props()))
                .description("Get all tasks with their id, name and status")
                .annotations(annotations(true, false, true))
                .build();
    }

    @Override
    protected Object execute(Map<String, Object> args) {
        return taskManager.findAll();
    }
}

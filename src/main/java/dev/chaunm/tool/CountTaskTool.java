package dev.chaunm.tool;

import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class CountTaskTool extends TaskTool {

    public CountTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("count_task", schema(props()))
                .description("Get the total number of tasks")
                .annotations(annotations(true, false, true))
                .build();
    }

    @Override
    protected Object execute(Map<String, Object> args) {
        return Map.of("count", taskManager.getSize());
    }
}

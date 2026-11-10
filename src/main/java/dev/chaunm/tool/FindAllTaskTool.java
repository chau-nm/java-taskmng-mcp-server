package dev.chaunm.tool;

import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.util.Map;

public class FindAllTaskTool implements McpTool {
    private final TaskManager taskManager;
    private final McpJsonMapper jsonMapper;

    public FindAllTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("find_all_task", Map.of(
                        "type", "object",
                        "properties", Map.of()))
                .description("Get all tasks with their id, name and status")
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        try {
            return McpSchema.CallToolResult.builder()
                    .addTextContent(jsonMapper.writeValueAsString(taskManager.findAll()))
                    .build();
        } catch (IOException e) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Could not read tasks: " + e.getMessage())
                    .isError(true)
                    .build();
        }
    }
}

package dev.chaunm.tool;

import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class CountTaskTool implements McpTool {
    private final TaskManager taskManager;

    public CountTaskTool(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("count_task", Map.of(
                        "type", "object",
                        "properties", Map.of()))
                .description("Get the total number of tasks")
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        return McpSchema.CallToolResult.builder()
                .addTextContent(String.valueOf(taskManager.getSize()))
                .build();
    }
}

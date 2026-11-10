package dev.chaunm.tool;

import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

public class RemoveTaskTool implements McpTool {
    private final TaskManager taskManager;

    public RemoveTaskTool(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("remove_task", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "id", Map.of("type", "integer")),
                        "required", List.of("id")))
                .description("Delete a task by its id")
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        long id = ((Number) request.arguments().get("id")).longValue();

        if (taskManager.findById(id).isEmpty()) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Task " + id + " not found")
                    .isError(true)
                    .build();
        }

        taskManager.remove(id);

        return McpSchema.CallToolResult.builder()
                .addTextContent("Task " + id + " removed")
                .build();
    }
}

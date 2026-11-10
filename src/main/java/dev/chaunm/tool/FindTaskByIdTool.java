package dev.chaunm.tool;

import dev.chaunm.core.Task;
import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FindTaskByIdTool implements McpTool {
    private final TaskManager taskManager;
    private final McpJsonMapper jsonMapper;

    public FindTaskByIdTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("find_task_by_id", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "id", Map.of("type", "integer")),
                        "required", List.of("id")))
                .description("Get one task by its id")
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        long id = ((Number) request.arguments().get("id")).longValue();

        Optional<Task> task = taskManager.findById(id);
        if (task.isEmpty()) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Task " + id + " not found")
                    .isError(true)
                    .build();
        }

        try {
            return McpSchema.CallToolResult.builder()
                    .addTextContent(jsonMapper.writeValueAsString(task.get()))
                    .build();
        } catch (IOException e) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Could not read task: " + e.getMessage())
                    .isError(true)
                    .build();
        }
    }
}

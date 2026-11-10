package dev.chaunm.tool;

import dev.chaunm.core.Task;
import dev.chaunm.core.TaskManager;
import dev.chaunm.core.TaskStatus;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UpdateTaskTool implements McpTool {
    private final TaskManager taskManager;
    private final McpJsonMapper jsonMapper;

    public UpdateTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("update_task", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "id", Map.of("type", "integer"),
                                "name", Map.of("type", "string"),
                                "status", Map.of("type", "string", "enum", List.of(
                                        "TODO", "DOING", "DONE", "PENDING", "CANCEL"))),
                        "required", List.of("id", "name", "status")))
                .description("Set the name and status of a task and return the updated task")
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        Map<String, Object> args = request.arguments();
        long id = ((Number) args.get("id")).longValue();
        String name = (String) args.get("name");
        TaskStatus status = TaskStatus.valueOf((String) args.get("status"));

        Optional<Task> task = taskManager.findById(id);
        if (task.isEmpty()) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Task " + id + " not found")
                    .isError(true)
                    .build();
        }

        taskManager.update(id, name, status);

        try {
            return McpSchema.CallToolResult.builder()
                    .addTextContent(jsonMapper.writeValueAsString(task.get()))
                    .build();
        } catch (IOException e) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Task updated but could not be read: " + e.getMessage())
                    .isError(true)
                    .build();
        }
    }
}

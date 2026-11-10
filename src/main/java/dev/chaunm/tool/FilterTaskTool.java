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

public class FilterTaskTool implements McpTool {
    private final TaskManager taskManager;
    private final McpJsonMapper jsonMapper;

    public FilterTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("filter_task", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "id", Map.of("type", "integer"),
                                "name", Map.of("type", "string", "description", "Text the task name contains"),
                                "status", Map.of("type", "string", "enum", List.of(
                                        "TODO", "DOING", "DONE", "PENDING", "CANCEL")))))
                .description("Find tasks by id, name and/or status. Every filter is optional.")
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        // Every filter is optional, so the client may send no arguments at all
        Map<String, Object> args = request.arguments() != null ? request.arguments() : Map.of();
        Long id = args.get("id") != null ? ((Number) args.get("id")).longValue() : null;
        String name = (String) args.get("name");
        TaskStatus status = args.get("status") != null ? TaskStatus.valueOf((String) args.get("status")) : null;

        List<Task> tasks = taskManager.filter(id, name, status);

        try {
            return McpSchema.CallToolResult.builder()
                    .addTextContent(jsonMapper.writeValueAsString(tasks))
                    .build();
        } catch (IOException e) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Could not read tasks: " + e.getMessage())
                    .isError(true)
                    .build();
        }
    }
}

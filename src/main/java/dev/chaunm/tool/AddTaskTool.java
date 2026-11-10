package dev.chaunm.tool;

import dev.chaunm.core.Task;
import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AddTaskTool implements McpTool {
    private final TaskManager taskManager;
    private final McpJsonMapper jsonMapper;

    public AddTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("add_task", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "name", Map.of("type", "string")),
                        "required", List.of("name")))
                .description("Create a new task with status TODO and return it")
                .build();
    }

    @Override
    public McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        String name = (String) request.arguments().get("name");

        Task task = new Task(name);
        taskManager.add(task);

        try {
            return McpSchema.CallToolResult.builder()
                    .addTextContent(jsonMapper.writeValueAsString(task))
                    .build();
        } catch (IOException e) {
            return McpSchema.CallToolResult.builder()
                    .addTextContent("Task added but could not be read: " + e.getMessage())
                    .isError(true)
                    .build();
        }
    }
}

package dev.chaunm;

import dev.chaunm.core.TaskManager;
import dev.chaunm.tool.*;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

public class Main {
    static void main() {
        McpJsonMapper jsonMapper = McpJsonDefaults.getMapper();
        TaskManager taskManager = new TaskManager();

        List<McpTool> tools = List.of(
                new CountTaskTool(taskManager, jsonMapper),
                new FindAllTaskTool(taskManager, jsonMapper),
                new FindTaskByIdTool(taskManager, jsonMapper),
                new FilterTaskTool(taskManager, jsonMapper),
                new AddTaskTool(taskManager, jsonMapper),
                new UpdateTaskTool(taskManager, jsonMapper),
                new RemoveTaskTool(taskManager, jsonMapper)
        );

        StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(jsonMapper);

        McpServer.sync(transportProvider)
                .serverInfo("taskmng", "1.0.0")
                .capabilities(
                        McpSchema.ServerCapabilities.builder()
                                .tools(false)
                                .build()
                )
                .tools(tools.stream().map(McpTool::toSpecification).toList())
                .build();
    }
}

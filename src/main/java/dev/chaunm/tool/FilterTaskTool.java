package dev.chaunm.tool;

import dev.chaunm.core.TaskManager;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class FilterTaskTool extends TaskTool {

    public FilterTaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public McpSchema.Tool definition() {
        return McpSchema.Tool.builder("filter_task", schema(props(
                        "id", idProperty(),
                        "name", nameProperty("Text the task name must contain (case-sensitive)"),
                        "status", statusProperty())))
                .description("Find tasks matching all given filters. Filters left out are ignored; "
                        + "with no filters every task is returned.")
                .annotations(annotations(true, false, true))
                .build();
    }

    @Override
    protected Object execute(Map<String, Object> args) {
        return taskManager.filter(optionalId(args), optionalString(args, "name"), optionalStatus(args));
    }
}

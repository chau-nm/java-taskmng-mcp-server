package dev.chaunm.tool;

import dev.chaunm.core.Task;
import dev.chaunm.core.TaskManager;
import dev.chaunm.core.TaskStatus;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for the task tools. Subclasses implement {@link #execute(Map)} and return any object;
 * this class turns it into JSON text, and turns a {@link ToolException} into an isError result.
 */
public abstract class TaskTool implements McpTool {
    protected static final List<String> STATUSES = Arrays.stream(TaskStatus.values()).map(Enum::name).toList();

    protected final TaskManager taskManager;
    private final McpJsonMapper jsonMapper;

    protected TaskTool(TaskManager taskManager, McpJsonMapper jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    /** Runs the tool with the call arguments (never null). Throw ToolException for bad input. */
    protected abstract Object execute(Map<String, Object> args);

    @Override
    public final McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request) {
        Map<String, Object> args = request.arguments() != null ? request.arguments() : Map.of();
        try {
            return result(jsonMapper.writeValueAsString(execute(args)), false);
        } catch (ToolException e) {
            return result(e.getMessage(), true);
        } catch (IOException e) {
            return result("Could not serialize the result: " + e.getMessage(), true);
        }
    }

    private static McpSchema.CallToolResult result(String text, boolean isError) {
        return McpSchema.CallToolResult.builder()
                .addTextContent(text)
                .isError(isError)
                .build();
    }

    // ---- building the tool definition ----

    /** Object schema with the given properties (in order) and required property names. */
    protected static Map<String, Object> schema(Map<String, Object> properties, String... required) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        if (required.length > 0) {
            schema.put("required", List.of(required));
        }
        schema.put("additionalProperties", false);
        return schema;
    }

    /** Ordered map: {@code props("a", x, "b", y)}. */
    protected static Map<String, Object> props(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    protected static Map<String, Object> idProperty() {
        return Map.of("type", "integer", "description", "Task id");
    }

    protected static Map<String, Object> nameProperty(String description) {
        return Map.of("type", "string", "minLength", 1, "description", description);
    }

    protected static Map<String, Object> statusProperty() {
        return Map.of("type", "string", "enum", STATUSES);
    }

    protected static McpSchema.ToolAnnotations annotations(boolean readOnly, boolean destructive, boolean idempotent) {
        return McpSchema.ToolAnnotations.builder()
                .readOnlyHint(readOnly)
                .destructiveHint(destructive)
                .idempotentHint(idempotent)
                .openWorldHint(false)
                .build();
    }

    // ---- reading arguments ----

    protected Task findTask(long id) {
        return taskManager.findById(id).orElseThrow(() -> new ToolException(
                "Task " + id + " not found. Call find_all_task to see the existing ids."));
    }

    protected static long requiredId(Map<String, Object> args) {
        Long id = optionalId(args);
        if (id == null) {
            throw new ToolException("id is required.");
        }
        return id;
    }

    protected static Long optionalId(Map<String, Object> args) {
        Object value = args.get("id");
        if (value == null) {
            return null;
        }
        if (value instanceof Integer || value instanceof Long) {
            return ((Number) value).longValue();
        }
        throw new ToolException("id must be an integer, got " + value + ".");
    }

    protected static String requiredString(Map<String, Object> args, String key) {
        String value = optionalString(args, key);
        if (value == null) {
            throw new ToolException(key + " is required.");
        }
        return value;
    }

    protected static String optionalString(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String s) || s.isBlank()) {
            throw new ToolException(key + " must be a non-empty string.");
        }
        return s;
    }

    protected static TaskStatus optionalStatus(Map<String, Object> args) {
        String value = optionalString(args, "status");
        if (value == null) {
            return null;
        }
        if (!STATUSES.contains(value)) {
            throw new ToolException("status must be one of " + STATUSES + ", got \"" + value + "\".");
        }
        return TaskStatus.valueOf(value);
    }
}

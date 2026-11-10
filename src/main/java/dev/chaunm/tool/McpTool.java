package dev.chaunm.tool;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

public interface McpTool {

    McpSchema.Tool definition();

    McpSchema.CallToolResult call(McpSyncServerExchange exchange, McpSchema.CallToolRequest request);

    default McpServerFeatures.SyncToolSpecification toSpecification() {
        return new McpServerFeatures.SyncToolSpecification(definition(), this::call);
    }
}

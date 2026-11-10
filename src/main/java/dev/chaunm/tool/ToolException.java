package dev.chaunm.tool;

/** An error the model should see and correct; returned as a result with isError: true. */
public class ToolException extends RuntimeException {
    public ToolException(String message) {
        super(message);
    }
}

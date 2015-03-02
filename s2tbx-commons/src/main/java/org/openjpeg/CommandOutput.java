package org.openjpeg;

/**
 * Created by Oscar on 16/02/2015.
 */
public class CommandOutput {
    private int errorCode;
    private String textOutput;
    private String errorOutput;

    public CommandOutput(int errorCode, String textOutput, String errorOutput) {
        this.errorCode = errorCode;
        this.textOutput = textOutput;
        this.errorOutput = errorOutput;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getTextOutput() {
        return textOutput;
    }

    public String getErrorOutput() {
        return errorOutput;
    }
}

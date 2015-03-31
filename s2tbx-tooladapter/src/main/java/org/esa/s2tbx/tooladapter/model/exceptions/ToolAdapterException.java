package org.esa.s2tbx.tooladapter.model.exceptions;

/** Base exception class for Standalone Tool Adapter.
 *
 * @author Lucian Barbulescu.
 */
public class ToolAdapterException extends Exception {

    /** Constructor.
     * @param message the exception message.
     */
    public ToolAdapterException(String message) {
        super(message);
    }

    /** Constructor.
     * @param message the exception message.
     * @param parent the parent cause of this exception
     */
    public ToolAdapterException(String message, Throwable parent) {
        super(message, parent);
    }
}

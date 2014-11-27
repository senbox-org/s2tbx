package org.esa.s2tbx.tooladapter.model.exceptions;

/** Exception used if data associated with a parameter is wrong.
 *
 * @author Lucian Barbulescu.
 */
public class InvalidParameterException extends ToolAdapterException {

    /** Constructor.
     * @param message the exception message.
     */
    public InvalidParameterException(String message) {
        super(message);
    }

    /** Constructor.
     * @param message the exception message.
     * @param parent the parent cause of this exception
     */
    public InvalidParameterException(String message, Throwable parent) {
        super(message, parent);
    }
}

package org.esa.s2tbx.tooladapter.model.exceptions;

/** Exception used if data associated with a template is wrong.
 *
 * @author Lucian Barbulescu.
 */
public class InvalidTemplateException extends ToolAdapterException {

    /** Constructor.
     * @param message the exception message.
     */
    public InvalidTemplateException(String message) {
        super(message);
    }

    /** Constructor.
     * @param message the exception message.
     * @param parent the parent cause of this exception
     */
    public InvalidTemplateException(String message, Throwable parent) {
        super(message, parent);
    }
}

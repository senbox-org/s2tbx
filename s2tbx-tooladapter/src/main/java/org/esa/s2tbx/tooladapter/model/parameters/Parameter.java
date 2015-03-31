package org.esa.s2tbx.tooladapter.model.parameters;

import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;

/** The interface of a tool's parameter that can be set by the user.
 *
 * @author Lucian Barbulescu.
 */
public interface Parameter {

    /** Set the name of a parameter.
     *
     * @param name the parameter name
     */
    public void setName(String name);

    /** Get the name of the parameter.
     *
     * @return the name of the parameter.
     */
    public String getName();

    /** Set a new value for the parameter by parsing the input string.
     * @param value the new value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException if the method is called for an undefined parameter
     */
    public void parseValue(String value) throws InvalidParameterException;

    /** Get the value of the parameter, as string.
     * @return the parameter value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException if the method is called for an undefined parameter
     */
    public String serializeValue() throws InvalidParameterException;

    /** Get the type of the parameter.
     * @return the parameter type.
     */
    public ParameterType getType();

    /** Get the tag of the parameter (the text that will be added to the template).
     * <p>
     *     The format of the tag is: ${name:type}. If type is undefined, the tag is ${name}.
     * </p>
     * @return the parameter tag.
     */
    public String getTag();

    /** Get the current object as an XML string.
     * @return the xml representation of the object.
     */
    public String toXmlString();
}

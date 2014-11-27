package org.esa.s2tbx.tooladapter.model.parameters;

import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;

/**
 * @author Lucian Barbulescu.
 */
public class TextParameter extends AbstractParameter {

    /** Create a text parameter.
     *
     * @param name the parameter name.
     */
    TextParameter(String name) {
        super(name, ParameterType.TEXT);
    }

    /**
     * Get the value of the parameter as a String.
     *
     * @return the string representation of the parameter's value.
     */
    @Override
    public String serializeValue() {
        return this.value;
    }

    /**
     * Set a new value for the parameter.
     *
     * @param value the new value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException if the method is called for an undefined parameter
     */
    @Override
    public void parseValue(String value) throws InvalidParameterException {
        this.value = value;
    }
}

package org.esa.s2tbx.tooladapter.model.parameters;

import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;

/**
 * @author Lucian Barbulescu.
 */
public class UndefinedParameter extends AbstractParameter {

    /** Constructor.
     *
     * @param name the parameter name
     */
    UndefinedParameter(String name) {
        super(name, ParameterType.UNDEFINED);
    }

    /**
     * Always throw an <code>org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException</code> .
     *
     * @return this type of parameters have no value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException
     */
    @Override
    public String serializeValue() throws InvalidParameterException {
        throw new InvalidParameterException("The 'undefined' type parameter " + this.name + " cannot have a value!");
    }

    /**
     * Always throw an <code>org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException</code> .
     *
     * @param value this type of parameters have no value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException
     */
    @Override
    public void parseValue(String value) throws InvalidParameterException {
        throw new InvalidParameterException("The 'undefined' type parameter " + this.name + " cannot have a value!");
    }
}

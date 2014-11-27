package org.esa.s2tbx.tooladapter.model.parameters;

import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;

/**
 * @author Lucian Barbulescu.
 */
public class FileTemplateParameter extends AbstractParameter {

    /** Create a template file parameter.
     *
     * @param name the parameter name.
     */
    FileTemplateParameter(String name) {
        super(name, ParameterType.FILETEMPLATE);
    }

    /** Set a new value for the parameter by parsing the input string.
     * @param value the new value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException if the method is called for an undefined parameter
     */
    public void parseValue(String value) throws InvalidParameterException {
        this.value = value;
    }

    /**
     * Get the value of the parameter, as string.
     *
     * @return the parameter value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException if the method is called for an undefined parameter
     */
    @Override
    public String serializeValue() throws InvalidParameterException {
        return this.value;
    }
}

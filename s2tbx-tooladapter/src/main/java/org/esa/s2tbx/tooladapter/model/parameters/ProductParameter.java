package org.esa.s2tbx.tooladapter.model.parameters;

import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;

/**
 * @author Lucian Barbulescu.
 */
public class ProductParameter extends AbstractParameter {

    /** Create a product parameter.
     *
     * @param name the parameter name.
     */
    ProductParameter(String name) {
        super(name, ParameterType.PRODUCT);
    }

    /**
     * The value identifies the type of the product, either 'source' or 'target'.
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

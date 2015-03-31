package org.esa.s2tbx.tooladapter.model.parameters;

/** An abstract implementation of a parameter.
 *
 * @author Lucian Barbulescu.
 */
public abstract class AbstractParameter implements Parameter {

    /** The parameter name. */
    protected String name;

    /** The value of the parameter. */
    protected String value;

    /** The type of the parameter. */
    protected ParameterType type;

    /** Create a new instance of the parameter.
     *
     * @param name the parameter name.
     * @param type  the type of the parameter.
     */
    AbstractParameter(String name, ParameterType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the parameter.
     *
     * @return the name of the parameter.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the type of the parameter.
     *
     * @return the parameter type.
     */
    @Override
    public ParameterType getType() {
        return this.type;
    }

    /**
     * Get the tag of the parameter (the text that will be added to the template).
     * <p>
     * The format of the tag is: ${name:type}. If type is undefined, the tag is ${name}.
     * </p>
     *
     * @return the parameter tag.
     */
    @Override
    public String getTag() {
        if (this.type == ParameterType.UNDEFINED) {
            return  "${" + this.name + "}";
        }
        return "${" + this.name + ":" + this.type + "}";
    }

    /**
     * Get the current object as an XML string.
     *
     * @return the xml representation of the object.
     */
    @Override
    public String toXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append('\t').append("<parameter ")
                .append("name=\"").append(this.name).append("\" ")
                .append("type=\"").append(this.type.toString()).append("\">")
                .append(this.value)
                .append("</parameter>\n");

        return sb.toString();
    }
}

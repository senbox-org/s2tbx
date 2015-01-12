package org.esa.s2tbx.tooladapter.model.parameters;

import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;

/** Singleton factory for parameters.
 *
 * @author Lucian Barbulescu.
 */
public class ParameterFactory {
    /** The instance of the singleton. */
    private static ParameterFactory _instance;

    static {
        //Generate the instance of the singleton
        ParameterFactory._instance = new ParameterFactory();
    }

    /**
     * Private constructor.
     */
    private ParameterFactory() {
        //Nothing to do
    }

    /** Get the singleton instance.
     * @return the singleton instance.
     */
    public static ParameterFactory instance() {
        return ParameterFactory._instance;
    }

    /**
     * Construct a parameter by parsing the associated tag.
     *
     * @param tag the input tag.
     * @return the parameter.
     */
    public Parameter buildParameter(String tag) throws InvalidParameterException {
        Parameter p = null;
        // strip the beginning and end of the tag
        String strippedTag = tag.substring(tag.indexOf('{')+1, tag.lastIndexOf('}'));
        //look for the existence of a type
        int index = strippedTag.indexOf(":");
        if (index == -1) {
            // no type is defined. An undefined parameter will be created with the name equal to the stripped tag
            p = new UndefinedParameter(strippedTag);
        } else {
            //extract the name and the type
            String name = strippedTag.substring(0, index);
            String typeText = strippedTag.substring(index + 1);
            ParameterType type = ParameterType.fromString(typeText);

            if (type == null) {
                throw new InvalidParameterException("Invalid parameter tag '" + tag + "': The type'" + typeText +
                        "' is unknown. Possible values are: 'text', 'file', 'filetemplate', product");
            }
            //based on the type build the corresponding parameter
            p = buildParameter(name, type);
        }

        return p;
    }

    public Parameter buildParameter(String name, ParameterType type) throws InvalidParameterException {
        Parameter p = null;
        if (type == null) {
            throw new InvalidParameterException("The type of the parameter cannot be null");
        }
        if (name == null) {
            throw new InvalidParameterException("The name of the parameter cannot be null");
        }
        //based on the type build the corresponding parameter
        switch (type) {
            case TEXT:
                p = new TextParameter(name);
                break;
            case FILE:
                p = new FileParameter(name);
                break;
            case FILETEMPLATE:
                p = new FileTemplateParameter(name);
                break;
            case PRODUCT:
                p = new ProductParameter(name);
                break;
            default:
                p = new UndefinedParameter(name);
        }
        return p;
    }
}

package org.esa.s2tbx.tooladapter.model.templates;

import org.esa.s2tbx.tooladapter.model.Tool;
import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;
import org.esa.s2tbx.tooladapter.model.exceptions.InvalidTemplateException;
import org.esa.s2tbx.tooladapter.model.exceptions.ToolAdapterException;
import org.esa.s2tbx.tooladapter.model.parameters.Parameter;
import org.esa.s2tbx.tooladapter.model.parameters.ParameterFactory;
import org.esa.s2tbx.tooladapter.model.parameters.ParameterType;

import java.util.HashMap;
import java.util.Map;

/** An abstract implementation of the template class.
 * @author Lucian Barbulescu.
 */
public abstract class AbstractTemplate implements Template {

    /** The name of the template. */
    protected String name;

    /** The content of the template. */
    protected String template;

    /** The type of the template. */
    protected String type;

    /** The parameters attached to this template. */
    protected Map<String, Parameter> parameters;

    /**
     * Constructor.
     */
    public AbstractTemplate(String type) {
        //initialise the parameters list
        this.type = type;
        this.parameters = new HashMap<String, Parameter>();
    }

    /**
     * Process the template.
     * <p>
     * The text of the template is saved in an internal member <br />
     * The parameters are extracted from the template.
     * </p>
     *
     * @param template the template text
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException if the parameters cannot be extracted
     */
    @Override
    public void parseTemplate(String template) throws InvalidParameterException {
        //Create a new list of parameters.
        Map<String, Parameter> newParameters = new HashMap<String, Parameter>();

        //Search for tags;
        int startTagIndex = 0;
        int endTagIndex = 0;
        do {
            startTagIndex = template.indexOf("${", endTagIndex);
            if (startTagIndex != -1) {
                endTagIndex = template.indexOf('}', startTagIndex);
                if (endTagIndex == -1) {
                    throw new InvalidParameterException("Error processing template. The end of tag starting at index " + startTagIndex + " is not found!");
                }

                //Get the tag
                String tag = template.substring(startTagIndex, endTagIndex+1);

                //Build the parameter;
                Parameter param = ParameterFactory.instance().buildParameter(tag);

                //check if the parameter already exists in the new mapping
                if (newParameters.containsKey(param.getName())) {
                    // get the parameter from the new mapping
                    Parameter duplicate = newParameters.get(param.getName());

                    //if the type is different an error is signaled
                    if (param.getType().compareTo(duplicate.getType()) != 0) {
                        throw new InvalidParameterException("Parameters with identical names and different types are not allowed!: Found '" + duplicate.getTag() + "' and '" + param.getTag() + "' within the template.");
                    }
                } else if (this.parameters.containsKey(param.getName())) {
                    // check if the parameter existed in the old mapping
                    // get the parameter from the old mapping
                    Parameter old = this.parameters.get(param.getName());

                    //if the type is the same and different from undefined, copy the value
                    if (    (old.getType() != ParameterType.UNDEFINED) &&
                            (old.getType().compareTo(param.getType()) == 0)) {
                        param.parseValue(old.serializeValue());
                    }
                }

                // save the parameter to the new list
                newParameters.put(param.getName(), param);
            }
        } while (startTagIndex != -1);

        //replace the parameters list
        this.parameters = newParameters;
        //save the template
        this.template = template;
    }

    /**
     * Get the name of the template, either "Command Line" or the file name.
     *
     * @return the name of the template.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of the template.
     *
     * @param name the template name.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the template text.
     *
     * @return the template text.
     */
    @Override
    public String getTemplate() {
        return this.template;
    }

    /**
     * Set the template text.
     *
     * @param template the template text.
     */
    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    /** Get the parameters.
     * @return the parameters.
     */
    @Override
    public Map<String, Parameter> getParameters() {
        return this.parameters;
    }

    /** Set the parameters.
     * @param parameters the parameters.
     */
    @Override
    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    /** Add a new parameter to the internal mapping
     * @param param the new parameter.
     * @throws InvalidParameterException if the parameter already exists.
     */
    @Override
    public void addParameter(Parameter param) throws InvalidParameterException {
        if (this.parameters.containsKey(param.getName())) {
            throw new InvalidParameterException("The parameter " + param.getName() + " already exists!");
        }
        this.parameters.put(param.getName(), param);
    }

    /** Replace the tags with the parameters values.
     *
     * @param tool             the parent tool
     * @param sourceProduct    the path to the source product
     * @param targetProduct    the path to the target product
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.ToolAdapterException if an error occurs
     */

    @Override
    public void processTemplate(Tool tool, String sourceProduct, String targetProduct) throws ToolAdapterException {
        String result = this.template;

        //loop through all parameters.
        for (Parameter param : this.parameters.values()) {
            // get the parameter's value as a string
            String value = processParameter(tool, sourceProduct, targetProduct, param);

            // get the parameter's tag
            String tag = param.getTag();

            //replace the tag with the value throughout the template
            result = result.replace(tag, value);
        }

        // post-process the data
        finalizeProcessing(result);
    }

    /** Process the parameter and get the value that will be added to the template.
     * @param tool             the parent tool
     * @param sourceProduct    the path to the source product
     * @param targetProduct    the path to the target product
     * @param param            the parameter
     * @return the processed value.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException in case of an error
     */
    protected String processParameter(Tool tool, String sourceProduct, String targetProduct, Parameter param) throws InvalidParameterException {
        String value = null;
        // some parameters require some special formatting
        switch (param.getType()) {
            case FILETEMPLATE:
                // the value of the parameter is the name of the template
                String name = param.serializeValue();
                //based on the name, extract the corresponding template.
                //in this implementation, only one file template is allowed, so only a validation is performed.
                if (!tool.getFileTemplate().getName().equalsIgnoreCase(name)) {
                    throw new InvalidParameterException("Unknown file template: " + name);
                }
                value = tool.getFileTemplate().getFilePath();
                break;
            case PRODUCT:
                // Check which product is actually required
                switch(param.serializeValue()) {
                    case "source":
                        value = sourceProduct;
                        break;
                    case "target":
                        value = targetProduct;
                        break;
                    default:
                        throw new InvalidParameterException("The value of a 'product' type parameter can be either 'source' or 'target'. Found " + param.serializeValue());
                }
                break;
            case UNDEFINED:
                // Undefined parameters are not accepted at this stage!
                throw new InvalidParameterException("Undefined parameter " + param.getName()+". Please specify a type for the parameter!");
            default:
                //just get the parameter value as a string.
                value = param.serializeValue();
                break;
        }



        //several

        return value;
    }

    /**
     * Get the current object as an XML string.
     *
     * @return the xml representation of the object.
     */
    @Override
    public String toXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append('\t').append("<template ")
                       .append("name=\"").append(this.name).append("\" ")
                       .append("type=\"").append(this.type).append("\">").append('\n');
        sb.append('\t').append("<content><![CDATA[").append(this.template).append("]]></content>").append('\n');
        for (Parameter param : this.parameters.values()) {
            sb.append('\t').append(param.toXmlString());
        }

        sb.append('\t').append("</template>");
        return sb.toString();
    }

    /**
     * Add template-specific processing on the data.
     *
     * @param data the data obtained after the tag-value replace
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidTemplateException if there are errors in processing the template
     */
    protected abstract void finalizeProcessing(String data) throws InvalidTemplateException;
}

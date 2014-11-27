package org.esa.s2tbx.tooladapter.model.templates;

import org.esa.s2tbx.tooladapter.model.Tool;
import org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException;
import org.esa.s2tbx.tooladapter.model.exceptions.ToolAdapterException;
import org.esa.s2tbx.tooladapter.model.parameters.Parameter;

import java.util.Map;

/** A template used to set a tool's parameters.
 *
 * @author Lucian Barbulescu.
 */
public interface Template {

    /** Get the name of the template, either "Command Line" or the file name.
     * @return the name of the template.
     */
    public String getName();

    /** Set the name of the template.
     * @param name the template name.
     */
    public void setName(String name);

    /** Get the template text.
     * @return the template text.
     */
    public String getTemplate();

    /** Set the template text.
     * @param template the template text.
     */
    public void setTemplate(String template);

    /** Get the parameters.
     * @return the parameters.
     */
    public Map<String, Parameter> getParameters();

    /** Set the parameters.
     * @param parameters the parameters.
     */
    public void setParameters(Map<String, Parameter> parameters);

    /** Add a new parameter to the internal mapping
     * @param param the new parameter.
     * @throws InvalidParameterException if the parameter already exists.
     */
    public void addParameter(Parameter param) throws InvalidParameterException;

    /** Parse the template.
     * <p>
     *     The text of the template is saved in an internal member <br />
     *     The parameters are extracted from the template.
     * </p>
     * @param template the template text
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidParameterException if the parameters cannot be extracted
     */
    public void parseTemplate(String template) throws InvalidParameterException;

    /** Replace the tags with the parameters values.
     *
     * @param tool             the parent tool
     * @param sourceProduct    the path to the source product
     * @param targetProduct    the path to the target product
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.ToolAdapterException if an error occurs
     */
    public void processTemplate(Tool tool, String sourceProduct, String targetProduct) throws ToolAdapterException;

    /** Get the current object as an XML string.
     * @return the xml representation of the object.
     */
    public String toXmlString();
}

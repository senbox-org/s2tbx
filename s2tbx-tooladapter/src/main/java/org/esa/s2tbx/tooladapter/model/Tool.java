package org.esa.s2tbx.tooladapter.model;

import org.esa.s2tbx.tooladapter.model.exceptions.ToolAdapterException;
import org.esa.s2tbx.tooladapter.model.templates.CommandLineTemplate;
import org.esa.s2tbx.tooladapter.model.templates.TextFileTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Metadata used to run an external tool.
 *
 * @author Lucian Barbulescu.
 */
public class Tool {

    /** The name of the tool. */
    private String name;

    /** The tool's file. */
    private File file;

    /** The tool's working directory. */
    private File workingDirectory;

    /** The required type of the source product. */
    private String sourceType;

    /** The template used for the command line of the tool or null if not used. */
    private CommandLineTemplate commandLineTemplate;

    /** The template used for the properties file of the tool. */
    private TextFileTemplate fileTemplate;

    /** The tool's descriptor. Not written to the descriptor itself! */
    private File descriptor;

    /**
     * Constructor.
     */
    public Tool() {
        this.name = null;
        this.file = null;
        this.descriptor = null;
        this.sourceType = null;
        this.workingDirectory = new File(System.getProperty("user.home"));
        this.commandLineTemplate = null;
        this.fileTemplate = null;
    }

    /** Get the name of the tool.
     * @return the name of the tool.
     */
    public String getName() {
        return this.name;
    }

    /** Set the name of the tool.
     * @param name the name of the tool.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Get the tool's executable file
     * @return the tool's executable.
     */
    public File getFile() {
        return this.file;
    }

    /** Set the tool's executable file.
     * @param file the tool's executable file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /** Get the required type of the source
     * @return the required type of the source
     */
    public String getSourceType() {
        return this.sourceType;
    }

    /** Set the required type pf the source.
     * @param sourceType the required type of the source
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /** Get the tool's descriptor file.
     * @return the tool's descriptor file
     */
    public File getDescriptor() {
        return this.descriptor;
    }

    /** Set the tool's descriptor file.
     * @param descriptor the new descriptor file.
     */
    public void setDescriptor(File descriptor) {
        this.descriptor = descriptor;
    }

    /** Get the tool's working directory.
     * @return the working directory.
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /** Set the tool's working directory.
     * @param workingDirectory the working directory.
     */
    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /** Get the template for the command line.
     * @return the template for the command line.
     */
    public CommandLineTemplate getCommandLineTemplate() {
        return this.commandLineTemplate;
    }

    /** Set the command line template.
     * @param commandLineTemplate the new command line template.
     */
    public void setCommandLineTemplate(CommandLineTemplate commandLineTemplate) {
        this.commandLineTemplate = commandLineTemplate;
    }

    /** Get the properties file template.
     * @return the properties file template.
     */
    public TextFileTemplate getFileTemplate() {
        return this.fileTemplate;
    }

    /** Set the properties files template.
     * @param fileTemplate the properties files template.
     */
    public void setFileTemplate(TextFileTemplate fileTemplate) {
        this.fileTemplate = fileTemplate;
    }

    /**
     * Fill the templates with data.
     * @param sourceProduct     the path to the source product.
     * @param targetProduct     the path to the target product.
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.ToolAdapterException in case of an error
     */
    public void fillTemplates(String sourceProduct, String targetProduct) throws ToolAdapterException {
        //build the command line
        if (this.commandLineTemplate != null) {
            this.commandLineTemplate.processTemplate(this, sourceProduct, targetProduct);
        }
        //build the configuration file
        if (this.fileTemplate != null) {
            this.fileTemplate.processTemplate(this, sourceProduct, targetProduct);
        }
    }

    /** Return the tool executable file and the command line.
     * @return the tool executable file and command line
     */
    public List<String> getToolWithCommandLine() {
        List<String> ret = new ArrayList<String>();
        // the first element is the tool's executable
        ret.add(this.file.getAbsolutePath());
        if (this.commandLineTemplate != null) {
            // add the command line arguments, if existing
            ret.addAll(this.commandLineTemplate.getCommandLine());
        }

        return ret;
    }

    /** Return the current object as an XML string.
     * @return the xml string.
     */
    public String toXmlString() {
        StringBuilder sb = new StringBuilder("<tool ")
                .append("name=\"").append(this.name).append("\" ")
                .append("sourceType=\"").append(this.sourceType).append("\">").append('\n');
        sb.append('\t').append("<toolFile>").append(this.file.getAbsolutePath()).append("</toolFile>").append('\n');
        sb.append('\t').append("<toolWorkingDirectory>").append(this.workingDirectory.getAbsolutePath()).append("</toolWorkingDirectory>").append('\n');
        if (this.commandLineTemplate != null) {
            sb.append(this.commandLineTemplate.toXmlString());
        }
        if (this.fileTemplate != null) {
            sb.append(this.fileTemplate.toXmlString());
        }
        sb.append("</tool>");
        return sb.toString();
    }
}

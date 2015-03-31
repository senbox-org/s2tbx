package org.esa.beam.framework.gpf.descriptor;

import com.bc.ceres.core.Assert;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.beam.util.StringUtils;
import org.esa.beam.utils.PrivilegedAccessor;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ramona Manda
 */
public class ToolAdapterOperatorDescriptor implements OperatorDescriptor {

    private String name;
    private Class<? extends Operator> operatorClass;
    private String alias;
    private String label;
    private String version;
    private String description;
    private String authors;
    private String copyright;
    private Boolean internal;
    private Boolean autoWriteSuppressed;

    private DefaultSourceProductDescriptor[] sourceProductDescriptors;
    private DefaultSourceProductsDescriptor sourceProductsDescriptor;
    private DefaultTargetProductDescriptor targetProductDescriptor;
    private DefaultTargetPropertyDescriptor[] targetPropertyDescriptors;

    private Boolean preprocessTool = false;
    private String preprocessorExternalTool;
    private Boolean writeForProcessing = false;
    private String processingWriter;
    private File mainToolFileLocation;
    private File workingDir;
    private String templateFileLocation;

    private String progressPattern;
    private String errorPattern;

    private List<SystemVariable> variables;

    private List<ToolParameterDescriptor> toolParameterDescriptors;

    ToolAdapterOperatorDescriptor() {
        this.sourceProductDescriptors = new DefaultSourceProductDescriptor[] { new DefaultSourceProductDescriptor() };
        try {
            PrivilegedAccessor.setValue(this.sourceProductDescriptors[0], "name", ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID);
            // this.sourceProductDescriptors[0].name = ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // this.sourceProductDescriptors[0].name = ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID;
        this.variables = new ArrayList<>();
        this.toolParameterDescriptors = new ArrayList<>();
    }

    public ToolAdapterOperatorDescriptor(String name, Class<? extends Operator> operatorClass) {
        this();
        this.name = name;
        this.operatorClass = operatorClass;
    }

    public ToolAdapterOperatorDescriptor(String name, Class<? extends Operator> operatorClass, String alias, String label, String version, String description, String authors, String copyright) {
        this(name, operatorClass);
        this.alias = alias;
        this.label = label;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.copyright = copyright;
    }

    public ToolAdapterOperatorDescriptor(ToolAdapterOperatorDescriptor obj) {
        this(obj.getName(), obj.getOperatorClass(), obj.getAlias(), obj.getLabel(), obj.getVersion(), obj.getDescription(), obj.getAuthors(), obj.getCopyright());
        this.internal = obj.isInternal();
        this.autoWriteSuppressed = obj.isAutoWriteDisabled();

        this.sourceProductDescriptors = new DefaultSourceProductDescriptor[obj.getSourceProductDescriptors().length];
        for (int i = 0; i < obj.getSourceProductDescriptors().length; i++) {
            this.sourceProductDescriptors[i] = ((DefaultSourceProductDescriptor) (obj.getSourceProductDescriptors()[i]));
        }

        this.sourceProductsDescriptor = (DefaultSourceProductsDescriptor) obj.getSourceProductsDescriptor();

        for (int i = 0; i < obj.getParameterDescriptors().length; i++) {
            this.toolParameterDescriptors.add(new ToolParameterDescriptor(obj.toolParameterDescriptors.get(i)));
        }

        this.targetProductDescriptor = (DefaultTargetProductDescriptor) obj.getTargetProductDescriptor();

        this.targetPropertyDescriptors = new DefaultTargetPropertyDescriptor[obj.getTargetPropertyDescriptors().length];
        for (int i = 0; i < obj.getTargetPropertyDescriptors().length; i++) {
            this.targetPropertyDescriptors[i] = ((DefaultTargetPropertyDescriptor) (obj.getTargetPropertyDescriptors()[i]));
        }
    }

    public ToolAdapterOperatorDescriptor(ToolAdapterOperatorDescriptor obj, String newName, String newAlias) {
        this(obj);
        this.name = newName;
        this.alias = newAlias;
        List<SystemVariable> variableList = obj.getVariables();
        if (variableList != null) {
            this.variables.addAll(variableList.stream()
                    .filter(systemVariable -> systemVariable != null)
                    .map(SystemVariable::createCopy).collect(Collectors.toList()));
        }
    }

    public void removeParamDescriptor(ToolParameterDescriptor descriptor) {
        this.toolParameterDescriptors.remove(descriptor);
    }

    public List<ToolParameterDescriptor> getToolParameterDescriptors() {
        return this.toolParameterDescriptors;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void seVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOperatorClass(Class<? extends Operator> operatorClass) {
        this.operatorClass = operatorClass;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getAuthors() {
        return authors;
    }

    @Override
    public String getCopyright() {
        return copyright;
    }

    @Override
    public boolean isInternal() {
        return internal != null ? internal : false;
    }

    @Override
    public boolean isAutoWriteDisabled() {
        return (autoWriteSuppressed != null && autoWriteSuppressed);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Class<? extends Operator> getOperatorClass() {
        return operatorClass != null ? operatorClass : Operator.class;
    }

    @Override
    public SourceProductDescriptor[] getSourceProductDescriptors() {
        return sourceProductDescriptors != null ? sourceProductDescriptors : new SourceProductDescriptor[0];
    }

    @Override
    public SourceProductsDescriptor getSourceProductsDescriptor() {
        return sourceProductsDescriptor;
    }

    @Override
    public ParameterDescriptor[] getParameterDescriptors() {
        //return parameterDescriptors != null ? parameterDescriptors : new ParameterDescriptor[0];
        ParameterDescriptor[] result = new ParameterDescriptor[0];
        return getToolParameterDescriptors().toArray(result);
    }

    @Override
    public TargetPropertyDescriptor[] getTargetPropertyDescriptors() {
        return targetPropertyDescriptors != null ? targetPropertyDescriptors : new TargetPropertyDescriptor[0];
    }

    @Override
    public TargetProductDescriptor getTargetProductDescriptor() {
        return targetProductDescriptor;
    }

    public String getTemplateFileLocation() {
        return templateFileLocation;
    }

    public void setTemplateFileLocation(String templateFileLocation) {
        this.templateFileLocation = templateFileLocation;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    public File getMainToolFileLocation() {
        return mainToolFileLocation;
    }

    public void setMainToolFileLocation(File mainToolFileLocation) {
        this.mainToolFileLocation = mainToolFileLocation;
    }

    public void setProgressPattern(String pattern) { this.progressPattern = pattern; }

    public String getProgressPattern() { return progressPattern; }

    public void setErrorPattern(String pattern) { this.errorPattern = pattern; }

    public String getErrorPattern() { return errorPattern; }

    public String getProcessingWriter() {
        return processingWriter;
    }

    public void setProcessingWriter(String processingWriter) {
        this.processingWriter = processingWriter;
    }

    public Boolean shouldWriteBeforeProcessing() {
        return writeForProcessing;
    }

    public void writeBeforeProcessing(Boolean writeForProcessing) {
        this.writeForProcessing = writeForProcessing;
    }

    public String getPreprocessorExternalTool() {
        return preprocessorExternalTool;
    }

    public void setPreprocessorExternalTool(String preprocessorExternalTool) {
        this.preprocessorExternalTool = preprocessorExternalTool;
    }

    public Boolean getPreprocessTool() {
        return preprocessTool;
    }

    public void setPreprocessTool(Boolean preprocessTool) {
        this.preprocessTool = preprocessTool;
    }

    public List<SystemVariable> getVariables() {
        return variables;
    }

    public void addVariable(SystemVariable variable) {
        this.variables.add(variable);
    }

    public ToolAdapterOperatorDescriptor createCopy() {
        return new ToolAdapterOperatorDescriptor(this, this.name, this.alias);
    }

    /**
     * Loads an operator descriptor from an XML document.
     *
     * @param url         The URL pointing to a valid operator descriptor XML document.
     * @param classLoader The class loader is used to load classed specified in the xml. For example the
     *                    class defined by the {@code operatorClass} tag.
     * @return A new operator descriptor.
     */
    public static ToolAdapterOperatorDescriptor fromXml(URL url, ClassLoader classLoader) {
        String resourceName = url.toExternalForm();
        try {
            try (InputStreamReader streamReader = new InputStreamReader(url.openStream())) {
                ToolAdapterOperatorDescriptor operatorDescriptor;
                operatorDescriptor = fromXml(streamReader, resourceName, classLoader);
                return operatorDescriptor;
            }
        } catch (IOException e) {
            throw new OperatorException(formatReadExceptionText(resourceName, e), e);
        }
    }

    /**
     * Loads an operator descriptor from an XML document.
     *
     * @param file        The file containing a valid operator descriptor XML document.
     * @param classLoader The class loader is used to load classed specified in the xml. For example the
     *                    class defined by the {@code operatorClass} tag.
     * @return A new operator descriptor.
     */
    public static ToolAdapterOperatorDescriptor fromXml(File file, ClassLoader classLoader) throws OperatorException {
        String resourceName = file.getPath();
        try {
            try (FileReader reader = new FileReader(file)) {
                return ToolAdapterOperatorDescriptor.fromXml(reader, resourceName, classLoader);
            }
        } catch (IOException e) {
            throw new OperatorException(formatReadExceptionText(resourceName, e), e);
        }
    }

    /**
     * Loads an operator descriptor from an XML document.
     *
     * @param reader       The reader providing a valid operator descriptor XML document.
     * @param resourceName Used in error messages
     * @param classLoader  The class loader is used to load classed specified in the xml. For example the
     *                     class defined by the {@code operatorClass} tag.
     * @return A new operator descriptor.
     */
    public static ToolAdapterOperatorDescriptor fromXml(Reader reader, String resourceName, ClassLoader classLoader) throws OperatorException {
        Assert.notNull(reader, "reader");
        Assert.notNull(resourceName, "resourceName");
        ToolAdapterOperatorDescriptor descriptor = new ToolAdapterOperatorDescriptor();
        try {
            createXStream(classLoader).fromXML(reader, descriptor);
            if (StringUtils.isNullOrEmpty(descriptor.getName())) {
                throw new OperatorException(formatInvalidExceptionMessage(resourceName, "missing 'name' element"));
            }
            if (StringUtils.isNullOrEmpty(descriptor.getAlias())) {
                throw new OperatorException(formatInvalidExceptionMessage(resourceName, "missing 'alias' element"));
            }
        } catch (StreamException e) {
            throw new OperatorException(formatReadExceptionText(resourceName, e), e);
        }
        return descriptor;
    }

    /**
     * Converts an operator descriptor to XML.
     *
     * @param classLoader The class loader is used to load classed specified in the xml. For example the
     *                    class defined by the {@code operatorClass} tag.
     * @return A string containing valid operator descriptor XML.
     */
    public String toXml(ClassLoader classLoader) {
        return createXStream(classLoader).toXML(this);
    }


    private static XStream createXStream(ClassLoader classLoader) {
        XStream xStream = new XStream();
        xStream.setClassLoader(classLoader);
        xStream.alias("operator", ToolAdapterOperatorDescriptor.class);

        xStream.alias("parameter", ToolParameterDescriptor.class);
        xStream.aliasField("parameters", ToolAdapterOperatorDescriptor.class, "toolParameterDescriptors");

        xStream.alias("variable", SystemVariable.class);
        xStream.aliasField("variables", ToolAdapterOperatorDescriptor.class, "variables");

        return xStream;
    }

    private static String formatReadExceptionText(String resourceName, Exception e) {
        return String.format("Failed to read operator descriptor from '%s':\nError: %s", resourceName, e.getMessage());
    }

    private static String formatInvalidExceptionMessage(String resourceName, String message) {
        return String.format("Invalid operator descriptor in '%s': %s", resourceName, message);
    }
}

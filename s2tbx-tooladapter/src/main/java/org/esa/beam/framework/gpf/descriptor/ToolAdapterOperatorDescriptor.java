package org.esa.beam.framework.gpf.descriptor;

import com.bc.ceres.core.Assert;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterConstants;
import org.esa.beam.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ramona Manda
 */
public class ToolAdapterOperatorDescriptor extends DefaultOperatorDescriptor {

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

    private List<SystemVariable> variables = new ArrayList<>();

    private List<ToolParameterDescriptor> tbxParameterDescriptors = new ArrayList<>();

    ToolAdapterOperatorDescriptor() {
        this.sourceProductDescriptors = new DefaultSourceProductDescriptor[1];
        this.sourceProductDescriptors[0] = new DefaultSourceProductDescriptor();
        this.sourceProductDescriptors[0].name = ToolAdapterConstants.TOOL_SOURCE_PRODUCT_ID;
        this.variables = new ArrayList<>();
        this.variables.add(new SystemVariable("key", "value"));
    }

    public ToolAdapterOperatorDescriptor(DefaultOperatorDescriptor obj) {

        this.name = obj.getName();
        this.operatorClass = obj.getOperatorClass();
        this.alias = obj.getAlias();
        this.label = obj.getLabel();
        this.version = obj.getVersion();
        this.description = obj.getDescription();
        this.authors = obj.getAuthors();
        this.copyright = obj.getCopyright();
        this.internal = obj.isInternal();
        this.autoWriteSuppressed = obj.isAutoWriteDisabled();

        this.sourceProductDescriptors = new DefaultSourceProductDescriptor[obj.getSourceProductDescriptors().length];
        for (int i = 0; i < obj.getSourceProductDescriptors().length; i++) {
            this.sourceProductDescriptors[i] = ((DefaultSourceProductDescriptor) (obj.getSourceProductDescriptors()[i]));
        }

        this.sourceProductsDescriptor = (DefaultSourceProductsDescriptor) obj.getSourceProductsDescriptor();

        this.tbxParameterDescriptors = new ArrayList<ToolParameterDescriptor>();
        for (int i = 0; i < obj.getParameterDescriptors().length; i++) {
            this.tbxParameterDescriptors.add(new ToolParameterDescriptor(obj.parameterDescriptors[i]));
        }

        this.targetProductDescriptor = (DefaultTargetProductDescriptor) obj.getTargetProductDescriptor();

        this.targetPropertyDescriptors = new DefaultTargetPropertyDescriptor[obj.getTargetPropertyDescriptors().length];
        for (int i = 0; i < obj.getTargetPropertyDescriptors().length; i++) {
            this.targetPropertyDescriptors[i] = ((DefaultTargetPropertyDescriptor) (obj.getTargetPropertyDescriptors()[i]));
        }

        this.variables = new ArrayList<>();
    }

    public ToolAdapterOperatorDescriptor(ToolAdapterOperatorDescriptor obj, String newName, String newAlias) {

        this.name = newName;
        this.alias = newAlias;
        this.operatorClass = obj.getOperatorClass();
        this.label = obj.getLabel();
        this.version = obj.getVersion();
        this.description = obj.getDescription();
        this.authors = obj.getAuthors();
        this.copyright = obj.getCopyright();
        this.internal = obj.isInternal();
        this.autoWriteSuppressed = obj.isAutoWriteDisabled();

        this.sourceProductDescriptors = new DefaultSourceProductDescriptor[obj.getSourceProductDescriptors().length];
        for (int i = 0; i < obj.getSourceProductDescriptors().length; i++) {
            this.sourceProductDescriptors[i] = ((DefaultSourceProductDescriptor) (obj.getSourceProductDescriptors()[i]));
        }

        this.sourceProductsDescriptor = (DefaultSourceProductsDescriptor) obj.getSourceProductsDescriptor();

        this.tbxParameterDescriptors = new ArrayList<>();
        for (int i = 0; i < obj.getS2tbxParameterDescriptors().size(); i++) {
            this.tbxParameterDescriptors.add(new ToolParameterDescriptor(obj.getS2tbxParameterDescriptors().get(i)));
        }

        this.targetProductDescriptor = (DefaultTargetProductDescriptor) obj.getTargetProductDescriptor();

        this.targetPropertyDescriptors = new DefaultTargetPropertyDescriptor[obj.getTargetPropertyDescriptors().length];
        for (int i = 0; i < obj.getTargetPropertyDescriptors().length; i++) {
            this.targetPropertyDescriptors[i] = ((DefaultTargetPropertyDescriptor) (obj.getTargetPropertyDescriptors()[i]));
        }

        this.variables = new ArrayList<>();
        for (int i = 0; i < obj.getVariables().size(); i++) {
            this.variables.add(obj.getVariables().get(i).createCopy());
        }
    }

    public ToolAdapterOperatorDescriptor(String name, Class<? extends Operator> operatorClass) {
        this.name = name;
        this.operatorClass = operatorClass;
        this.variables = new ArrayList<>();
    }

    public ToolAdapterOperatorDescriptor(String name, Class<? extends Operator> operatorClass, String alias, String label, String version, String description, String authors, String copyright) {
        this.name = name;
        this.operatorClass = operatorClass;
        this.alias = alias;
        this.label = label;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.copyright = copyright;
        this.variables = new ArrayList<>();
    }

    public void removeParamDescriptor(ToolParameterDescriptor descriptor) {
        this.tbxParameterDescriptors.remove(descriptor);
    }

    public List<ToolParameterDescriptor> getS2tbxParameterDescriptors() {
        /*if(this.parameterDescriptors.length != this.tbxParameterDescriptors.size()) {
            this.tbxParameterDescriptors.clear();
            for (int i = 0; i < operators.length; i++) {
                retOperators[i] = (ToolParameterDescriptor) operators[i];
            }
        }*/
        return this.tbxParameterDescriptors;
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
        return autoWriteSuppressed != null ? autoWriteSuppressed : false;
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
        return getS2tbxParameterDescriptors().toArray(result);
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

    public String getProcessingWriter() {
        return processingWriter;
    }

    public void setProcessingWriter(String processingWriter) {
        this.processingWriter = processingWriter;
    }

    public Boolean getWriteForProcessing() {
        return writeForProcessing;
    }

    public void setWriteForProcessing(Boolean writeForProcessing) {
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

        //xStream.alias("sourceProduct", DefaultSourceProductDescriptor.class);
        //xStream.aliasField("namedSourceProducts", ToolAdapterOperatorDescriptor.class, "sourceProductDescriptors");

        //xStream.alias("sourceProducts", DefaultSourceProductsDescriptor.class);
        //xStream.aliasField("sourceProducts", ToolAdapterOperatorDescriptor.class, "sourceProductsDescriptor");

        xStream.alias("parameter", ToolParameterDescriptor.class);
        xStream.aliasField("parameters", ToolAdapterOperatorDescriptor.class, "tbxParameterDescriptors");

        //xStream.alias("targetProduct", DefaultTargetProductDescriptor.class);
        //xStream.aliasField("targetProduct", ToolAdapterOperatorDescriptor.class, "targetProductDescriptor");

        //xStream.alias("targetProperty", DefaultTargetPropertyDescriptor.class);
        //xStream.aliasField("targetProperties", ToolAdapterOperatorDescriptor.class, "targetPropertyDescriptors");

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

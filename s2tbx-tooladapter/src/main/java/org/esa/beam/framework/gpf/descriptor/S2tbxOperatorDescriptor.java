package org.esa.beam.framework.gpf.descriptor;

import com.bc.ceres.core.Assert;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductWriter;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by ramonag on 1/24/2015.
 */
public class S2tbxOperatorDescriptor extends DefaultOperatorDescriptor {

    String name;
    Class<? extends Operator> operatorClass;
    String alias;
    String label;
    String version;
    String description;
    String authors;
    String copyright;
    Boolean internal;
    Boolean autoWriteSuppressed;

    DefaultSourceProductDescriptor[] sourceProductDescriptors;
    DefaultSourceProductsDescriptor sourceProductsDescriptor;
    DefaultParameterDescriptor[] parameterDescriptors;
    DefaultTargetProductDescriptor targetProductDescriptor;
    DefaultTargetPropertyDescriptor[] targetPropertyDescriptors;

    Boolean preprocessTool = false;
    String preprocessingExternalTool;
    Boolean writeForProcessing = false;
    String processingWriter;
    File mainToolFileLocation;
    File workingDir;
    String templateFileLocation;

    S2tbxSystemVariable[] variables;

    public static final Class<?> readerSuperClass = ProductReader.class;
    public static final Class<?> writerSuperClass = ProductWriter.class;

    private List<S2tbxParameterDescriptor> tbxParameterDescriptors = new ArrayList<S2tbxParameterDescriptor>();

    S2tbxOperatorDescriptor(){super(null, null);}

    public S2tbxOperatorDescriptor(DefaultOperatorDescriptor obj){
        super(obj.getName(), obj.getOperatorClass());

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
        for(int i=0; i<obj.getSourceProductDescriptors().length;i++){
            this.sourceProductDescriptors[i] = ((DefaultSourceProductDescriptor)(obj.getSourceProductDescriptors()[i]));
        }

        this.sourceProductsDescriptor = (DefaultSourceProductsDescriptor)obj.getSourceProductsDescriptor();

        this.parameterDescriptors = new DefaultParameterDescriptor[obj.getParameterDescriptors().length];
        this.tbxParameterDescriptors = new ArrayList<S2tbxParameterDescriptor>();
        for(int i=0; i<obj.getParameterDescriptors().length;i++){
            this.parameterDescriptors[i] = ((DefaultParameterDescriptor)(obj.getParameterDescriptors()[i]));
            this.tbxParameterDescriptors.add(new S2tbxParameterDescriptor(this.parameterDescriptors[i]));
        }

        this.targetProductDescriptor = (DefaultTargetProductDescriptor)obj.getTargetProductDescriptor();

        this.targetPropertyDescriptors = new DefaultTargetPropertyDescriptor[obj.getTargetPropertyDescriptors().length];
        for(int i=0; i<obj.getTargetPropertyDescriptors().length;i++){
            this.targetPropertyDescriptors[i] = ((DefaultTargetPropertyDescriptor)(obj.getTargetPropertyDescriptors()[i]));
        }

        this.variables = new S2tbxSystemVariable[0];
    }

    public S2tbxOperatorDescriptor(S2tbxOperatorDescriptor obj, String newName, String newAlias){
        super(newName, obj.getOperatorClass());

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
        for(int i=0; i<obj.getSourceProductDescriptors().length;i++){
            this.sourceProductDescriptors[i] = ((DefaultSourceProductDescriptor)(obj.getSourceProductDescriptors()[i]));
        }

        this.sourceProductsDescriptor = (DefaultSourceProductsDescriptor)obj.getSourceProductsDescriptor();

        this.parameterDescriptors = new DefaultParameterDescriptor[obj.getParameterDescriptors().length];
        for(int i=0; i<obj.getParameterDescriptors().length;i++){
            this.parameterDescriptors[i] = ((DefaultParameterDescriptor)(obj.getParameterDescriptors()[i]));
        }
        for(int i=0; i<obj.getS2tbxParameterDescriptors().size();i++){
            this.tbxParameterDescriptors.add(new S2tbxParameterDescriptor(obj.getS2tbxParameterDescriptors().get(i)));
        }

        this.targetProductDescriptor = (DefaultTargetProductDescriptor)obj.getTargetProductDescriptor();

        this.targetPropertyDescriptors = new DefaultTargetPropertyDescriptor[obj.getTargetPropertyDescriptors().length];
        for(int i=0; i<obj.getTargetPropertyDescriptors().length;i++){
            this.targetPropertyDescriptors[i] = ((DefaultTargetPropertyDescriptor)(obj.getTargetPropertyDescriptors()[i]));
        }

        this.variables = new S2tbxSystemVariable[0];
    }

    public S2tbxOperatorDescriptor(String name, Class<? extends Operator> operatorClass){
        super(name, operatorClass);
    }

    public S2tbxOperatorDescriptor(String name, Class<? extends Operator> operatorClass, String alias, String label, String version, String description, String authors, String copyright){
     super(name, operatorClass);
        this.alias = alias;
        this.label = label;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.copyright = copyright;
    }

    public void removeParamDescriptor(S2tbxParameterDescriptor descriptor){
        //List<DefaultParameterDescriptor> list = new ArrayList<DefaultParameterDescriptor>(Arrays.asList(parameterDescriptors));
        //list.removeAll(Arrays.asList((DefaultParameterDescriptor)descriptor));
        //parameterDescriptors = list.toArray(new DefaultParameterDescriptor[list.size()]);

        this.tbxParameterDescriptors.remove(descriptor);
    }

    public S2tbxParameterDescriptor getFirstParamDescriptorByName(String name){
        try {
            S2tbxParameterDescriptor filtered = tbxParameterDescriptors.stream().filter(p -> p.getName().equals(name)).findFirst().get();
            return filtered;
        }catch (NoSuchElementException ex) {return null;}
    }

    public void addParamDescriptor(S2tbxParameterDescriptor descriptor){
        List<DefaultParameterDescriptor> list = new ArrayList<DefaultParameterDescriptor>(Arrays.asList(parameterDescriptors));
        list.add((DefaultParameterDescriptor)descriptor);
        parameterDescriptors = list.toArray(new DefaultParameterDescriptor[list.size()]);
        this.tbxParameterDescriptors.add(descriptor);
    }

    public List<S2tbxParameterDescriptor> getS2tbxParameterDescriptors(){
        /*if(this.parameterDescriptors.length != this.tbxParameterDescriptors.size()) {
            this.tbxParameterDescriptors.clear();
            for (int i = 0; i < operators.length; i++) {
                retOperators[i] = (S2tbxParameterDescriptor) operators[i];
            }
        }*/
        return this.tbxParameterDescriptors;
    }

    public void setAlias(String alias){
        this.alias = alias;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public void seVersion(String version){
        this.version = version;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setAuthors(String authors){
        this.authors = authors;
    }

    public void setCopyright(String copyright){
        this.copyright = copyright;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setOperatorClass(Class<? extends Operator> operatorClass){
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

    public String getPreprocessingExternalTool() {
        return preprocessingExternalTool;
    }

    public void setPreprocessingExternalTool(String preprocessingExternalTool) {
        this.preprocessingExternalTool = preprocessingExternalTool;
    }

    public Boolean getPreprocessTool() {
        return preprocessTool;
    }

    public void setPreprocessTool(Boolean preprocessTool) {
        this.preprocessTool = preprocessTool;
    }

    public S2tbxSystemVariable[] getVariables() {
        return variables;
    }

    public void setVariables(S2tbxSystemVariable[] variables) {
        this.variables = variables;
    }

    /**
     * Loads an operator descriptor from an XML document.
     *
     * @param url         The URL pointing to a valid operator descriptor XML document.
     * @param classLoader The class loader is used to load classed specified in the xml. For example the
     *                    class defined by the {@code operatorClass} tag.
     *
     * @return A new operator descriptor.
     */
    public static S2tbxOperatorDescriptor fromXml(URL url, ClassLoader classLoader) {
        String resourceName = url.toExternalForm();
        try {
            try (InputStreamReader streamReader = new InputStreamReader(url.openStream())) {
                S2tbxOperatorDescriptor operatorDescriptor;
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
     *
     * @return A new operator descriptor.
     */
    public static S2tbxOperatorDescriptor fromXml(File file, ClassLoader classLoader) throws OperatorException {
        String resourceName = file.getPath();
        try {
            try (FileReader reader = new FileReader(file)) {
                return S2tbxOperatorDescriptor.fromXml(reader, resourceName, classLoader);
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
     *
     * @return A new operator descriptor.
     */
    public static S2tbxOperatorDescriptor fromXml(Reader reader, String resourceName, ClassLoader classLoader) throws OperatorException {
        Assert.notNull(reader, "reader");
        Assert.notNull(resourceName, "resourceName");
        S2tbxOperatorDescriptor descriptor = new S2tbxOperatorDescriptor();
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
     *
     * @return A string containing valid operator descriptor XML.
     */
    public String toXml(ClassLoader classLoader) {
        return createXStream(classLoader).toXML(this);
    }


    private static XStream createXStream(ClassLoader classLoader) {
        XStream xStream = new XStream();

        xStream.setClassLoader(classLoader);

        xStream.alias("operator", S2tbxOperatorDescriptor.class);

        xStream.alias("sourceProduct", DefaultSourceProductDescriptor.class);
        xStream.aliasField("namedSourceProducts", S2tbxOperatorDescriptor.class, "sourceProductDescriptors");

        xStream.alias("sourceProducts", DefaultSourceProductsDescriptor.class);
        xStream.aliasField("sourceProducts", S2tbxOperatorDescriptor.class, "sourceProductsDescriptor");

        xStream.alias("parameter", S2tbxParameterDescriptor.class);
        xStream.aliasField("parameters", S2tbxOperatorDescriptor.class, "tbxParameterDescriptors");

        xStream.alias("targetProduct", DefaultTargetProductDescriptor.class);
        xStream.aliasField("targetProduct", S2tbxOperatorDescriptor.class, "targetProductDescriptor");

        xStream.alias("targetProperty", DefaultTargetPropertyDescriptor.class);
        xStream.aliasField("targetProperties", S2tbxOperatorDescriptor.class, "targetPropertyDescriptors");

        xStream.alias("variable", S2tbxSystemVariable.class);
        xStream.aliasField("variables", S2tbxOperatorDescriptor.class, "variables");

        return xStream;
    }

    private static String formatReadExceptionText(String resourceName, Exception e) {
        return String.format("Failed to read operator descriptor from '%s':\nError: %s", resourceName, e.getMessage());
    }

    private static String formatInvalidExceptionMessage(String resourceName, String message) {
        return String.format("Invalid operator descriptor in '%s': %s", resourceName, message);
    }
}

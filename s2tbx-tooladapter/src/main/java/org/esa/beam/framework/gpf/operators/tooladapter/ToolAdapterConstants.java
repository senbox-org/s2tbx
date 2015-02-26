package org.esa.beam.framework.gpf.operators.tooladapter;

import java.io.File;

/**
 * @author Lucian Barbulescu.
 */
public interface ToolAdapterConstants {

    /**
     * The root folder for the tool adapter descriptors.
     */
    public static final String TOOL_ADAPTER_REPO = "META-INF" + File.separator + "services" + File.separator + "tools" + File.separator;

    /**
     * The id of the tool's source product.
     */
    public static final String TOOL_SOURCE_PRODUCT_ID = "sourceProduct";

    /**
     * The id of the tool's target file as it is used in the descriptor.
     */
    public static final String TOOL_TARGET_PRODUCT_FILE_ID = "targetProductFile";

    public static final String TOOL_SOURCE_PRODUCT_FILE = "sourceProductFile";

    public static final String OPERATOR_DEFAULT_NAME_PREFIX = "org.esa.beam.framework.gpf.operators.tooladapter";

    public static final String OPERATOR_GENERATED_NAME_SEPARATOR = "_";

    public static String OPERATOR_FILE_SUFIX = "-info.xml";

    public static String TOOL_VELO_TEMPLATE_SUFIX = "-template.vm";

    public static String TOOL_CMD_TEMPLATE_SUFIX = "-cmdLineTemplate.tpl";

    /* Comment the following line and uncomment the next one when reading from a configured location */
    public static String DESCRIPTOR_FILE = "descriptor.xml";
    /*
    public static String DESCRIPTOR_FILE = "META-INF" + File.separator + "descriptor.xml";
    */
    public static String SPI_FILE = "META-INF" + File.separator + "services" + File.separator + "org.esa.beam.framework.gpf.OperatorSpi";
    public static String SPI_FILE_CONTENT = "org.esa.beam.framework.gpf.operators.tooladapter.ToolAdapterOpSpi";

}

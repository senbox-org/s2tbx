package org.esa.s2tbx.tooladapter;

import java.io.File;

/**
 * @author Lucian Barbulescu.
 */
public interface S2tbxToolAdapterConstants {

    /**
     * The root folder for the tool adapter descriptors.
     */
    public static final String TOOL_ADAPTER_REPO = "META-INF" + File.separator + "services" + File.separator + "tools" + File.separator;

    /**
     * The name of the file that contains the list of registered tools.
     */
    public static final String TOOL_ADAPTER_DB = "tools_db";

    /**
     * The id of the tool's source product.
     */
    public static final String TOOL_SOURCE_PRODUCT_ID = "sourceProduct";

    /**
     * The id of the tool's target file as it is used in the descriptor.
     */
    public static final String TOOL_TARGET_PRODUCT_FILE_ID = "targetProductFile";

    public static final String TOOL_SOURCE_PRODUCT_FILE = "sourceProductFile";

    public static final String OPERATOR_DEFAULT_NAME_PREFIX = "org.esa.s2tbx.tooladapter";

    public static final String OPERATOR_GENERATED_NAME_SEPARATOR = "_";

    public static String OPERATOR_FILE_SUFIX = "-info.xml";

    public static String TOOL_VELO_TEMPLATE_SUFIX = "-template.vm";

    public static String TOOL_CMD_TEMPLATE_SUFIX = "-cmdLineTemplate.tpl";
}

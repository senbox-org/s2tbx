package org.esa.s2tbx.tooladapter;

/**
 * @author Lucian Barbulescu.
 */
public interface S2tbxToolAdapterConstants {

    /** The root folder for the tool adapter descriptors. */
    public static final String TOOL_ADAPTER_REPO = "META-INF/services/tools/";

    /** The name of the file that contains the list of registered tools. */
    public static final String TOOL_ADAPTER_DB = "tools_db";

    /** The id of the tool's source product.*/
    public static final String TOOL_SOURCE_PRODUCT_ID = "sourceProduct";

    /** The id of the tool's target file as it is used in the descriptor.*/
    public static final String TOOL_TARGET_PRODUCT_FILE_ID = "targetProductFile";

    /** The id of the tool file as it is used in the descriptor.*/
    public static final String TOOL_FILE_NAME_ID = "toolFile";

    /** The id of the tool working directory as it is used in the descriptor.*/
    public static final String TOOL_WORKING_DIR_ID = "toolWorkingDirectory";

    /** The id of the tool's command line template as it is used in the descriptor.*/
    public static final String TOOL_CMD_LINE_TMPL_ID = "commandLineTemplate";
}

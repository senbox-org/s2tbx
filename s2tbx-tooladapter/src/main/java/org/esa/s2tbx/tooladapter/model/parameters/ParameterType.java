package org.esa.s2tbx.tooladapter.model.parameters;

/** The type identifiers for the parameters.
 *
 * @author Lucian Barbulescu.
 */
public enum ParameterType {
    /** the type of the parameter is unknown. */
    UNDEFINED(""),
    /** a simple string parameter. */
    TEXT("text"),
    /** an external file. */
    FILE("file"),
    /** a file generated from a template. */
    FILETEMPLATE("filetemplate"),
    /** a product. */
    PRODUCT("product");

    /** The string representation of the parameter type. */
    private String type;

    /** Constructor.
     * @param type the type
     */
    ParameterType(String type) {
        this.type = type;
    }

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return this.type;
    }

    /** Build an enum object from text.
     *
     * @param typeText the type as a text
     * @return the enum object or null
     */
    public static ParameterType fromString(String typeText) {
        if (typeText != null) {
            for (ParameterType pt : ParameterType.values()) {
                if (typeText.equalsIgnoreCase(pt.type)) {
                    return pt;
                }
            }
        }
        return null;
    }
}


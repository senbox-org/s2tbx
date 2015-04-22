package org.esa.snap.framework.gpf.descriptor;

/**
 * This class encapsulates an environment (or system) variable
 * that can be passed to a tool adapter operator.
 *
 * @author Ramona Manda
 */
public class SystemVariable {
    String key;
    String value;

    SystemVariable() {
        this.key = "";
        this.value = "";
    }

    public SystemVariable(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the name of the system variable.
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the name of the system variable.
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value of the system variable
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the system variable
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *  Creates a copy of this SystemVariable instance.
     *
     * @return  A copy of this instance
     */
    public SystemVariable createCopy() {
        SystemVariable newVariable = new SystemVariable();
        newVariable.setKey(this.key);
        newVariable.setValue(this.value);
        return newVariable;
    }

}

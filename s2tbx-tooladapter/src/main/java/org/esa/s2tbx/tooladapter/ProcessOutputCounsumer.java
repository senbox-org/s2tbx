package org.esa.s2tbx.tooladapter;

/** This interface is used to consume the output of a tool, line by line
 * @author Lucian Barbulescu.
 */
public interface ProcessOutputCounsumer {
    /** Consume a line of output obtained from a tool.
     * @param line a line of output text.
     */
    public void consumeOutpuLine(String line);
}

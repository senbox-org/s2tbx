package org.esa.s2tbx.tooladapter.model.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/** The template for the command line.
 * @author Lucian Barbulescu.
 */
public class CommandLineTemplate extends AbstractTemplate {

    /** The tool's command line arguments. */
    private List<String> commandLineArgs;

    /**
     * Constructor.
     */
    public CommandLineTemplate() {
        super("commandline");
        setName("Command Line");
        this.commandLineArgs = new ArrayList<String>();
    }


    /**
     * Add template-specific processing on the data and fill the <code>commandLine</code> member.
     *
     * @param data the data obtained after the tag-value replace
     */
    @Override
    protected void finalizeProcessing(String data) {
        // remove any new line characters
        this.commandLineArgs.clear();

        //split the command line into components
        StringTokenizer st = new StringTokenizer(data, "\r\n");
        while (st.hasMoreTokens()) {
            String s = st.nextToken().trim();
            if (s.length() > 0) {
                this.commandLineArgs.add(s);
            }
        }
    }

    /** Get the command line arguments.
     * @return the command line arguments.
     */
    public List<String> getCommandLine() {
        return commandLineArgs;
    }

}

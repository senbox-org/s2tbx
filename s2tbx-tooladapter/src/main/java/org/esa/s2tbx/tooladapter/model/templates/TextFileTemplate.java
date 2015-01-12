package org.esa.s2tbx.tooladapter.model.templates;

import org.esa.s2tbx.tooladapter.model.exceptions.InvalidTemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Lucian Barbulescu.
 */
public class TextFileTemplate extends AbstractTemplate {

    /** The folder where this file will be created. */
    protected File fileLocation;

    /**
     * Constructor.
     */
    public TextFileTemplate() {
        super("text");
        this.fileLocation = null;
        this.name = null;
    }

    /**
     * Add template-specific processing on the data.
     * <p>
     *     Save the text to the external file.
     * </p>
     *
     * @param data the data obtained after the tag-value replace
     * @throws org.esa.s2tbx.tooladapter.model.exceptions.InvalidTemplateException if the file cannot be created
     */
    @Override
    protected void finalizeProcessing(String data) throws InvalidTemplateException {
        //check if the file name is set
        if (this.name == null || this.name.equalsIgnoreCase("")) {
            throw new InvalidTemplateException("You must specify a name for the template file.");
        }
        // check if the file location is set and exists
        if(this.fileLocation == null || !this.fileLocation.isDirectory()) {
            throw new InvalidTemplateException("The folder where the file "+this.name+" must be created is invalid!");
        }
        //save the file
        File target = new File (this.fileLocation, this.name);
        FileWriter fw = null;
        try {
            //create a file writer
            fw = new FileWriter(target);
            //write the content
            fw.write(data);
            //close the file
            fw.close();
        } catch (IOException e) {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e1) {
                    //nothing to do!
                }
            }
            //throw exception
            throw new InvalidTemplateException("Error creating the parameters file", e);
        }
    }

    /** Set the folder where this file will be created.
     * @param fileLocation the destination folder.
     */
    public void setFileLocation(File fileLocation) {
        this.fileLocation = fileLocation;
    }

    /** Get the file complete path.
     * @return the file path.
     */
    public String getFilePath() {
        return new File(this.fileLocation, this.name).getAbsolutePath();
    }
}

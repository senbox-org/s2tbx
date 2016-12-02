package org.esa.s2tbx.dataio.gdal;

import org.esa.snap.utils.StringHelper;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by jcoravu on 2/12/2016.
 */
public class ExportDriversFileFilter extends FileFilter {
    private final String description;
    private final String extension;

    public ExportDriversFileFilter(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }

    @Override
    public boolean accept(File fileToAccept) {
        if (fileToAccept.isDirectory()) {
            return true;
        }
        return StringHelper.endsWithIgnoreCase(fileToAccept.getName(), this.extension);
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}

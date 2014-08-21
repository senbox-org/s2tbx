package org.esa.beam.dataio.spot.internal;

import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.dataio.spot.dimap.SpotConstants;

import java.io.File;
import java.io.IOException;

/**
 * This is a specialisation of the more generic ZipVirtualDir.
 * The reason of creating this class is the possibility of encountering
 * different capitalisation of files inside zip archives.
 *
 * @author  Cosmin Cara
 */
public class SpotVirtualDir extends ZipVirtualDir {
    /**
     * Constructor that wraps a virtual directory over a file or folder source.
     *
     * @param source The source file or folder.
     * @throws java.io.IOException
     */
    public SpotVirtualDir(File source) throws IOException {
        super(source);
    }

    @Override
    protected boolean correctCapitalisation() throws IOException {
        String[] list = wrappedVirtualDir.list("");
        while (list.length == 1) {
            unnecessaryPath += list[0];
            list = wrappedVirtualDir.list(unnecessaryPath);
            for (String file : list) {
                if (SpotConstants.DIMAP_VOLUME_FILE.equalsIgnoreCase(file)) {
                    shouldConvertCase = !SpotConstants.DIMAP_VOLUME_FILE.equals(file);
                }
            }
        }
        return shouldConvertCase;
    }
}

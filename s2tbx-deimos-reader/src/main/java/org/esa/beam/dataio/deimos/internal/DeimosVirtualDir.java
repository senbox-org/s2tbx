package org.esa.beam.dataio.deimos.internal;

import org.esa.beam.dataio.ZipVirtualDir;

import java.io.File;
import java.io.IOException;

/**
 * Created by kraftek on 9/23/2014.
 */
public class DeimosVirtualDir extends ZipVirtualDir {

    private boolean isUnpacked;
    /**
     * Constructor that wraps a virtual directory over a file or folder source.
     *
     * @param source The source file or folder.
     * @throws java.io.IOException
     */
    public DeimosVirtualDir(File source) throws IOException {
        super(source);
    }

    @Override
    public File getFile(String relativePath) throws IOException {
        return isUnpacked ? new File(wrappedVirtualDir.getTempDir() + File.separator + relativePath) : super.getFile(relativePath);
    }

    @Override
    public String[] listAll() {
        String[] list = super.listAll();
        if (isZipFile)
            isUnpacked = true;
        return list;
    }
}

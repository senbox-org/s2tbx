package org.esa.beam.dataio.deimos.internal;

import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kraftek on 9/23/2014.
 */
public class DeimosVirtualDir extends ZipVirtualDir {

    private boolean isUnpacked;
    protected Map<String, String> files;
    /**
     * Constructor that wraps a virtual directory over a file or folder source.
     *
     * @param source The source file or folder.
     * @throws java.io.IOException
     */
    public DeimosVirtualDir(File source) throws IOException {
        super(source);
        files = new HashMap<String, String>();
    }

    @Override
    public File getFile(String relativePath) throws IOException {
        File file = null;
        String key = FileUtils.getFileNameFromPath(relativePath).toLowerCase();
        String path = findKeyFile(key);
        if (path == null)
            throw new IOException(String.format("File %s does not exist", relativePath));
        relativePath = path;
        try {
            // the "classic" way
            file = super.getFile(relativePath);
        } catch (IOException e) {
            file = isUnpacked ? new File(wrappedVirtualDir.getTempDir() + File.separator + relativePath) : super.getFile(relativePath);
        }
        return file;
    }

    @Override
    public String[] listAll() {
        String[] list = super.listAll();
        if (isZipFile)
            isUnpacked = true;
        for (String item : list)
            files.put(FileUtils.getFileNameFromPath(item).toLowerCase(), item);
        return list;
    }

    private String findKeyFile(String key) {
        if (key == null || key.isEmpty())
            return null;
        String ret = files.get(key);
        if (ret == null) {
            Iterator<String> iterator = files.keySet().iterator();
            String namePart = FileUtils.getFilenameWithoutExtension(FileUtils.getFileNameFromPath(key));
            String extPart = FileUtils.getExtension(key);
            while (iterator.hasNext()) {
                String current = iterator.next();
                String name = FileUtils.getFilenameWithoutExtension(FileUtils.getFileNameFromPath(current));
                name = name.substring(name.lastIndexOf("/") + 1);
                String ext = FileUtils.getExtension(current);
                if (extPart.equalsIgnoreCase(ext) &&
                        namePart.startsWith(name)) {
                    ret = files.get(current);
                    break;
                }
            }
        }
        return ret;
    }
}

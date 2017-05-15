package org.esa.s2tbx.dataio.gdal.reader.plugins;

import org.esa.s2tbx.dataio.gdal.activator.GDALInstallInfo;
import org.esa.s2tbx.dataio.gdal.reader.GDALProductReader;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.utils.StringHelper;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Reader plugin for products using the GDAL library.
 *
 * @author Jean Coravu
 */
public abstract class AbstractDriverProductReaderPlugIn implements ProductReaderPlugIn {
    private final Set<String> extensions;
    private final String driverName;
    private final String driverDisplayName;
    private final String pluginFormatName;

    protected AbstractDriverProductReaderPlugIn(String driverName, String driverDisplayName) {
        this.extensions = new HashSet<String>();
        this.driverName = driverName;
        this.driverDisplayName = driverDisplayName;
        this.pluginFormatName = "GDAL-" + driverName + "-READER";
    }

    protected AbstractDriverProductReaderPlugIn(String extension, String driverShortName, String driverLongName) {
        this(driverShortName, driverLongName);

        addExtensin(extension);
    }

    @Override
    public final String getDescription(Locale locale) {
        return this.driverDisplayName;
    }

    @Override
    public final String[] getFormatNames() {
        return new String[] { this.pluginFormatName };
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        if (GDALInstallInfo.INSTANCE.isPresent()) {
            String filePath = null;
            if (input instanceof String) {
                filePath = (String)input;
            } else if (input instanceof File) {
                filePath = ((File)input).getAbsolutePath();
            } else if (input instanceof Path) {
                filePath = ((Path)input).toFile().getAbsolutePath();
            } else {
                throw new IllegalArgumentException("Unknown type '"+input.getClass()+"' for input '"+ input.toString()+"'.");
            }
            Iterator<String> it = this.extensions.iterator();
            while (it.hasNext()) {
                String extension = it.next();
                if (StringHelper.endsWithIgnoreCase(filePath, extension)) {
                    return DecodeQualification.SUITABLE;
                }
            }
        }
        return DecodeQualification.UNABLE;
    }

    @Override
    public final Class[] getInputTypes() {
        return new Class[] { String.class, File.class };
    }

    @Override
    public final ProductReader createReaderInstance() {
        return new GDALProductReader(this);
    }

    @Override
    public final String[] getDefaultFileExtensions() {
        String[] defaultExtensions = new String[this.extensions.size()];
        this.extensions.toArray(defaultExtensions);
        return defaultExtensions;
    }

    @Override
    public final SnapFileFilter getProductFileFilter() {
        return new SnapFileFilter(getFormatNames()[0], getDefaultFileExtensions(), getDescription(Locale.getDefault()));
    }

    protected final void addExtensin(String extension) {
        this.extensions.add(extension);
    }

    public final String getDriverName() {
        return driverName;
    }
}


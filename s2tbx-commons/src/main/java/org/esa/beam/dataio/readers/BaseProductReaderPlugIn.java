package org.esa.beam.dataio.readers;

import org.esa.beam.dataio.VirtualDirEx;
import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.io.BeamFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by kraftek on 11/26/2014.
 */
public abstract class BaseProductReaderPlugIn implements ProductReaderPlugIn {

    public BaseProductReaderPlugIn() {
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification retVal = DecodeQualification.UNABLE;
        VirtualDirEx virtualDir;
        try {
            virtualDir = getInput(input);
            if (virtualDir != null) {
                String[] allFiles = virtualDir.listAll();
                ProductContentEnforcer enforcer = ProductContentEnforcer.create(getMinimalPatternList(), getExclusionPatternList());
                if (enforcer.isConsistent(allFiles)) {
                    retVal = DecodeQualification.INTENDED;
                }
            }
        } catch (IOException e) {
            retVal = DecodeQualification.UNABLE;
        }
        return retVal;
    }

    @Override
    public abstract Class[] getInputTypes();

    @Override
    public abstract ProductReader createReaderInstance();

    @Override
    public abstract String[] getFormatNames();

    @Override
    public abstract String[] getDefaultFileExtensions();

    @Override
    public abstract String getDescription(Locale locale);

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BaseProductFileFilter(this);
    }

    protected abstract String[] getProductFilePatterns();

    protected abstract String[] getMinimalPatternList();

    protected abstract String[] getExclusionPatternList();

    public VirtualDirEx getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);
        if (inputFile.isFile() && !VirtualDirEx.isPackedFile(inputFile)) {
            final File absoluteFile = inputFile.getAbsoluteFile();
            inputFile = absoluteFile.getParentFile();
            if (inputFile == null) {
                throw new IOException("Unable to retrieve parent to file: " + absoluteFile.getAbsolutePath());
            }
        }
        return VirtualDirEx.create(inputFile);
    }

    protected File getFileInput(Object input) {
        File outFile = null;
        if (input instanceof String) {
            outFile = new File((String) input);
        } else if (input instanceof File) {
            outFile = (File) input;
        }
        return outFile;
    }

    public class BaseProductFileFilter extends BeamFileFilter {

        private BaseProductReaderPlugIn parent;

        public BaseProductFileFilter() {
            super();
        }

        public BaseProductFileFilter(BaseProductReaderPlugIn plugIn) {
            this();
            this.parent = plugIn;
            setFormatName(parent.getFormatNames()[0]);
            setDescription(parent.getDescription(Locale.getDefault()));
            setExtensions(parent.getDefaultFileExtensions());
        }

        @Override
        public boolean accept(File file) {
            boolean shouldAccept = super.accept(file);
            if (file.isFile() && !VirtualDirEx.isPackedFile(file)) {
                File folder = file.getParentFile();
                String[] list = folder.list();
                boolean consistent = true;
                for (String pattern : getProductFilePatterns()) {
                    for (String fName : list) {
                        String lcName = fName.toLowerCase();
                        if (!pattern.endsWith("zip"))
                            shouldAccept = lcName.matches(pattern);
                        if (shouldAccept) break;
                    }
                    consistent &= shouldAccept;
                }
                shouldAccept = consistent;
            }
            return shouldAccept;
        }
    }
}

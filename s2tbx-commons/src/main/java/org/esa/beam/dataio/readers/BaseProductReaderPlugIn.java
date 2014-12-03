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
 * Base class for product reader plugins which follow the logic of checking consistency
 * of products using naming consistency rules.
 *
 * @see org.esa.beam.dataio.readers.ProductContentEnforcer
 * @author Cosmin Cara
 */
public abstract class BaseProductReaderPlugIn implements ProductReaderPlugIn {

    protected final ProductContentEnforcer enforcer;

    /**
     * Default constructor
     */
    public BaseProductReaderPlugIn() {
        enforcer = ProductContentEnforcer.create(getMinimalPatternList(), getExclusionPatternList());
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification retVal = DecodeQualification.UNABLE;
        VirtualDirEx virtualDir;
        try {
            virtualDir = getInput(input);
            if (virtualDir != null) {
                String[] allFiles = virtualDir.listAll();
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

    /**
     * Returns the list of possible file patterns of a product.
     * @return  The list of regular expressions.
     */
    protected abstract String[] getProductFilePatterns();

    /**
     * Returns the minimal list of file patterns of a product.
     * @return  The list of regular expressions.
     */
    protected abstract String[] getMinimalPatternList();

    /**
     * Returns the exclusion list (i.e. anti-patterns) of a product.
     * @return  The list of regular expressions.
     */
    protected abstract String[] getExclusionPatternList();

    /**
     * Returns an abstraction of the given input.
     * If the input is a (not compressed or packed) file, it returns a <code>com.bc.ceres.core.VirtualDir.File</code> object.
     * If the input is a folder, it returns a <code>com.bc.ceres.core.VirtualDir.Dir</code> object.
     * If the input is either a tar file or a tgz file, it returns a <code>org.sa.beam.dataio.VirtualDirEx.TarVirtualDir</code> object.
     * If the input is a compressed file, it returns a wrapper over a <code>com.bc.ceres.core.VirtualDir.Zip</code> object.
     * @param input The input object
     * @return  An instance of a VirtualDir or VirtualDirEx implementations.
     * @throws IOException  If unable to retrieve the parent of the input.
     */
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

    /**
     * Default implementation for a file filter using product naming rules.
     */
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
            if (shouldAccept && file.isFile() && !VirtualDirEx.isPackedFile(file)) {
                File folder = file.getParentFile();
                String[] list = folder.list();
                shouldAccept &= enforcer.isConsistent(list);
            }
            return shouldAccept;
        }
    }
}

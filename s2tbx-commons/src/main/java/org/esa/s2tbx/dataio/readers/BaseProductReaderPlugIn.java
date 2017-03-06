/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.readers;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.util.io.SnapFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Base class for product reader plugins which follow the logic of checking consistency
 * of products using naming consistency rules.
 *
 * @see org.esa.s2tbx.dataio.readers.ProductContentEnforcer
 * @author Cosmin Cara
 */
public abstract class BaseProductReaderPlugIn implements ProductReaderPlugIn {

    private static Map<Object, String[]> cachedFiles = new WeakHashMap<>();
    protected final ProductContentEnforcer enforcer;
    protected int folderDepth;

    /**
     * Default constructor
     */
    public BaseProductReaderPlugIn() {
        folderDepth = 1;
        String[] patternList = getMinimalPatternList();
        for (String pattern : patternList) {
            folderDepth = Math.max(folderDepth, pattern.split("\\[/").length - 1);
        }
        enforcer = ProductContentEnforcer.create(patternList, getExclusionPatternList());
        registerRGBProfile();
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        DecodeQualification retVal = DecodeQualification.UNABLE;
        VirtualDirEx virtualDir;
        try {
            virtualDir = getInput(input);
            if (virtualDir != null) {
                String[] files = null;
                if (virtualDir.isCompressed()) {
                    if (!cachedFiles.containsKey(input)) {
                        cachedFiles.put(input, virtualDir.listAll());
                    }
                    files = cachedFiles.get(input);
                    if (enforcer.isConsistent(files)) {
                        retVal = DecodeQualification.INTENDED;
                    }
                } else {
                    Pattern[] patternList = enforcer.getMinimalFilePatternList();
                    File inputFile = getFileInput(input);
                    if (inputFile.isFile() &&
                            Arrays.stream(patternList).anyMatch(p -> p.matcher(inputFile.getName()).matches())) {
                        virtualDir.setFolderDepth(folderDepth);
                        files = virtualDir.listAll(patternList);
                        if (files.length >= patternList.length && enforcer.isConsistent(files)) {
                            retVal = DecodeQualification.INTENDED;
                        }
                    }
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
    public SnapFileFilter getProductFileFilter() {
        return new BaseProductFileFilter(this, folderDepth);
    }

    /**
     * Returns the list of possible file patterns of a product.
     * @return  The list of regular expressions.
     *//*
    protected abstract String[] getProductFilePatterns();
*/
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

    /**
     * Returns the input object as a File object.
     * @param input the plugin input
     * @return  a File object instance
     */
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
     * Registers a RGB profile for the reader plugin.
     */
    protected abstract void registerRGBProfile();

    /**
     * Returns the list of files in a folder, up to the given depth of the folder,
     * using NIO API.
     *
     * @param parent    The parent folder
     * @param depth     The depth to look for files
     * @return The list of files
     * @throws IOException
     */
    static List<String> listFiles(File parent, int depth) throws IOException {
        if (parent == null)
            return null;
        List<String> files = new ArrayList<>();
        Files.walkFileTree(Paths.get(parent.getAbsolutePath()),
                EnumSet.noneOf(FileVisitOption.class),
                depth,
                new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        files.add(file.toFile().getAbsolutePath().replace(parent.getAbsolutePath(), "").substring(1));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
        return files;
    }

    /**
     * Default implementation for a file filter using product naming rules.
     */
    public class BaseProductFileFilter extends SnapFileFilter {

        private Map<File, Boolean> processed;
        final private int depth;

        public BaseProductFileFilter(BaseProductReaderPlugIn plugIn, int folderDepth) {
            super(plugIn.getFormatNames()[0], plugIn.getDefaultFileExtensions(), plugIn.getDescription(Locale.getDefault()));
            this.processed = new HashMap<>();
            this.depth = folderDepth;
        }

        @Override
        public boolean accept(File file) {
            boolean shouldAccept = super.accept(file);
            if (shouldAccept && file.isFile() && !VirtualDirEx.isPackedFile(file)) {
                File folder = file.getParentFile();
                if (!processed.containsKey(folder)) {
                    try {
                        List<String> files = listFiles(folder, depth);
                        shouldAccept = enforcer.isConsistent(files);
                        processed.put(folder, shouldAccept);
                    } catch (IOException e) {
                        Logger.getLogger(BaseProductFileFilter.class.getName()).warning(e.getMessage());
                    }
                } else {
                    shouldAccept = processed.get(folder);
                }
            }
            return shouldAccept;
        }

    }
}

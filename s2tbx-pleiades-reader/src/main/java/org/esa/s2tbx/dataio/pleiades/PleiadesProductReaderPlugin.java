/*
 * Copyright (C) 2014-2016 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2016 CS-Romania (office@c-s.ro)
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
package org.esa.s2tbx.dataio.pleiades;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.pleiades.dimap.Constants;
import org.esa.s2tbx.dataio.pleiades.internal.PleiadesMetadataInspector;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.metadata.MetadataInspector;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.RGBImageProfile;
import org.esa.snap.core.datamodel.RGBImageProfileManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Reader plugin for Pleiades products.
 *
 * @author Cosmin Cara
 */
public class PleiadesProductReaderPlugin extends BaseProductReaderPlugIn {

    private static final String COLOR_PALETTE_FILE_NAME = "Pleiades_color_palette.cpd";

    public PleiadesProductReaderPlugin() {
        super("org/esa/s2tbx/dataio/pleiades/" + PleiadesProductReaderPlugin.COLOR_PALETTE_FILE_NAME);

        this.folderDepth = 3;
    }

    @Override
    public MetadataInspector getMetadataInspector() {
        return new PleiadesMetadataInspector();
    }

    @Override
    public Class[] getInputTypes() {
        return Constants.READER_INPUT_TYPES;
    }

    @Override
    public ProductReader createReaderInstance() {
        return new PleiadesProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return Constants.FORMAT_NAMES;
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return Constants.DEFAULT_EXTENSIONS;
    }

    @Override
    public String getDescription(Locale locale) {
        return Constants.DIMAP_DESCRIPTION;
    }

    @Override
    protected String[] getMinimalPatternList() {
        return Constants.MINIMAL_FILES_PATTERN_LIST;
    }

    @Override
    protected String[] getExclusionPatternList() {
        return new String[0];
    }

    @Override
    protected void registerRGBProfile() {
        RGBImageProfileManager.getInstance().addProfile(new RGBImageProfile("Pleiades", Constants.RGB_PROFILE));
    }

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        Path inputPath = convertInputToPath(input);
        try (VirtualDirEx virtualDir = VirtualDirEx.build(inputPath, false, true)){
            DecodeQualification returnValue = DecodeQualification.UNABLE;
            if (virtualDir != null) {
                Pattern[] patternList = new Pattern[Constants.MINIMAL_PATTERN_LIST.length];
                for (int i = 0; i < Constants.MINIMAL_PATTERN_LIST.length; i++) {
                    patternList[i] = Pattern.compile(Constants.MINIMAL_PATTERN_LIST[i], Pattern.CASE_INSENSITIVE);
                }
                String[] filteredFiles = null;
                if (virtualDir.isCompressed()) {
                    // list all the files without filters and apply the filters later
                    String[] availableFiles = virtualDir.listAll();
                    if (availableFiles == null) {
                        //throw new NullPointerException("The files array is null."); //getDecodeQualification should not throw any exception
                        return DecodeQualification.UNABLE;
                    }
                    // apply the reader plugin filters
                    List<String> filteredFileNames = new ArrayList<>();
                    for (String availableFile : availableFiles) {
                        if (VirtualDirEx.matchFilters(availableFile, patternList)) {
                            filteredFileNames.add(availableFile);
                        }
                    }
                    filteredFiles = new String[filteredFileNames.size()];
                    filteredFileNames.toArray(filteredFiles);
                } else if (Files.isRegularFile(inputPath)) {
                    boolean matches = Arrays.stream(patternList).anyMatch(p -> p.matcher(inputPath.getFileName().toString()).matches());
                    if (matches) {
                        filteredFiles = virtualDir.listAll(patternList);
                    }
                }
                if (filteredFiles != null && filteredFiles.length >= patternList.length && this.enforcer.isConsistent(filteredFiles)) {
                    returnValue = DecodeQualification.INTENDED;
                }
            }
            return returnValue;
        } catch (Throwable e) { //getDecodeQualification should not throw any exception, so use Throwable instead of IOException
            return DecodeQualification.UNABLE;
        }
    }
}

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

package org.esa.s2tbx.dataio.spot.dimap;

import org.apache.commons.lang.StringUtils;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder for SPOT4 TAKE5 metadata file.
 *
 * @author Ramona Manda
 */
public class SpotTake5Metadata extends XmlMetadata {

    private String metadataFileName;
    private Map<String, String> tiffFiles;
    private Map<String, String> maskFiles;
    private String metaSubFolder;

    /**
     * Parameter for the SPOT4 TAKE5 products
     *
     * @param name the name of the metadata format
     */
    public SpotTake5Metadata(String name) {
        super(name);
        this.tiffFiles = null;
        this.metaSubFolder = "";
    }

    public String getMetaSubFolder() {
        return metaSubFolder;
    }

    public void setMetaSubFolder(String metaSubFolder) {
        this.metaSubFolder = metaSubFolder;
    }

    @Override
    public String getFileName() {
        return this.metadataFileName;
    }

    @Override
    public int getNumBands() {
        int numBands = -1;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_RADIOMETRY)) != null)) {
            String descBands = currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_BANDS);
            numBands = StringUtils.countMatches(descBands, SpotConstants.SPOT4_TAKE5_VALUES_SEPARATOR) + 1;
        }
        return numBands;
    }

    /**
     * This method returns the name of the bands, as they appear in the metadata file, under the tag METADATA/RADIOMETRY/BANDS
     *
     * @return a list of String values, representing the name of the bands from the metadata
     */
    public String[] getBandNames() {
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_RADIOMETRY)) != null)) {
            String descBands = currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_BANDS);
            return StringUtils.split(descBands, SpotConstants.SPOT4_TAKE5_VALUES_SEPARATOR);
        }
        return null;
    }

    /**
     * This method returns the name of the tiff files, as they appear in the metadata file, under the tags:
     * <ul><li>METADATA/FILES/GEOTIFF</li></ul>
     * <ul><li>METADATA/FILES/ORTHO_SURF_AOT</li></ul>
     * <ul><li>METADATA/FILES/ORTHO_SURF_CORR_ENV</li></ul>
     * <ul><li>METADATA/FILES/ORTHO_SURF_CORR_PENTE</li></ul>
     * <ul><li>METADATA/FILES/ORTHO_VAP_EAU</li></ul>
     *
     * @return a map of String values, representing pairs of the tag of the file and the file name, as they appear in the metadata file
     */
    public Map<String, String> getTiffFiles() {
        if (this.tiffFiles != null && this.tiffFiles.size() > 0) {
            return this.tiffFiles;
        }
        MetadataAttribute currentElement;
        MetadataElement filesElement;
        if (((filesElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_FILES)) != null)) {
            this.tiffFiles = new HashMap<String, String>();
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_GEOTIFF)) != null) {
                this.tiffFiles.put(SpotConstants.SPOT4_TAKE5_TAG_GEOTIFF, currentElement.getData().toString());
            }
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_AOT)) != null) {
                this.tiffFiles.put(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_AOT, currentElement.getData().toString());
            }
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_CORR_ENV)) != null) {
                this.tiffFiles.put(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_CORR_ENV, currentElement.getData().toString());
            }
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_CORR_PENTE)) != null) {
                this.tiffFiles.put(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_SURF_CORR_PENTE, currentElement.getData().toString());
            }
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_VAP_EAU)) != null) {
                this.tiffFiles.put(SpotConstants.SPOT4_TAKE5_TAG_ORTHO_VAP_EAU, currentElement.getData().toString());
            }
        }
        return this.tiffFiles;
    }

    /**
     * This method returns the name of the tiff files for the masks, as they appear in the metadata file, under the tags:
     * <ul><li>METADATA/FILES/MASK_SATURATION</li></ul>
     * <ul><li>METADATA/FILES/MASK_CLOUDS</li></ul>
     * <ul><li>METADATA/FILES/MASK_DIVERSE</li></ul>
     * NB: for N2 products, the metadata contains the name of the grouping folder, in which the mask files are to be found
     *
     * @return a map of String values, representing pairs of the tag of the file and the file name, as they appear in the metadata file
     */
    public Map<String, String> getMaskFiles() {
        if (this.maskFiles != null && this.maskFiles.size() > 0) {
            return this.maskFiles;
        }
        MetadataAttribute currentElement;
        MetadataElement filesElement;
        if (((filesElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_FILES)) != null)) {
            this.maskFiles = new HashMap<>();
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_SATURATION)) != null) {
                this.maskFiles.put(SpotConstants.SPOT4_TAKE5_TAG_SATURATION, currentElement.getData().toString());
            }
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_CLOUDS)) != null) {
                this.maskFiles.put(SpotConstants.SPOT4_TAKE5_TAG_CLOUDS, currentElement.getData().toString());
            }
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_DIVERSE)) != null) {
                this.maskFiles.put(SpotConstants.SPOT4_TAKE5_TAG_DIVERSE, currentElement.getData().toString());
            }
        }
        return this.maskFiles;
    }

    public String getMasksFolder() {
        String maskFolderName = null;
        MetadataAttribute currentElement;
        MetadataElement filesElement;
        if (((filesElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_FILES)) != null)) {
            if ((currentElement = filesElement.getAttribute(SpotConstants.SPOT4_TAKE5_TAG_MASK_N2)) != null) {
                ProductData data = currentElement.getData();
                maskFolderName = data != null ? data.toString() : null;
            }
        }
        return maskFolderName;
    }

    @Override
    public String getProductName() {
        String name = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_HEADER)) != null)) {
            name = currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_IDENT);
            rootElement.setDescription(name);
        }
        return name;
    }

    /**
     * Reads the projection code, as it appears in the metadata file, under the tag METADATA/GEOMETRY/PROJECTION
     *
     * @return the String value representing the projection code, as it appears in the metadata file
     */
    public String getProjectionCode() {
        String name = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_GEOMETRY)) != null)) {
            name = currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_PROJECTION);
        }
        return name;
    }

    /**
     * Reads the Y dimension of the raster, as it appears in the metadata file, under the tag METADATA/GEOMETRY/ORIGIN_Y
     *
     * @return the float value representing the Y dimension of the raster, as it appears in the metadata file
     */
    public float getRasterGeoRefY() {
        float value = 0.0f;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_GEOMETRY)) != null)) {
            value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_ORIGIN_Y));
        }
        return value;
    }

    /**
     * Reads the X dimension of the raster, as it appears in the metadata file, under the tag METADATA/GEOMETRY/ORIGIN_X
     *
     * @return the float value representing the X dimension of the raster, as it appears in the metadata file
     */
    public float getRasterGeoRefX() {
        float value = 0.0f;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_GEOMETRY)) != null)) {
            value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_ORIGIN_X));
        }
        return value;
    }

    /**
     * Reads the Y dimension of the raster size, as it appears in the metadata file, under the tag METADATA/GEOMETRY/PIXEL_SIZE_Y
     *
     * @return the float value representing the Y dimension of the raster size, as it appears in the metadata file
     */
    public float getRasterGeoRefSizeY() {
        float value = 0.0f;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_GEOMETRY)) != null)) {
            value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_PIXEL_SIZE_Y));
        }
        return value;
    }

    /**
     * Reads the X dimension of the raster size, as it appears in the metadata file, under the tag METADATA/GEOMETRY/PIXEL_SIZE_X
     *
     * @return the float value representing the X dimension of the raster size, as it appears in the metadata file
     */
    public float getRasterGeoRefSizeX() {
        float value = 0.0f;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_GEOMETRY)) != null)) {
            value = Float.parseFloat(currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_PIXEL_SIZE_X));
        }
        return value;
    }

    @Override
    public String getFormatName() {
        return SpotConstants.SPOT4_TAKE5_FORMAT;
    }

    @Override
    public String getMetadataProfile() {
        String name = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_HEADER)) != null)) {
            name = currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_LEVEL);
        }
        return name;
    }

    @Override
    public int getRasterWidth() {
        if (width == 0) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_GEOMETRY)) != null)) {
                width = Integer.parseInt(currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_COLS));
            }
        }
        return width;
    }

    @Override
    public int getRasterHeight() {
        if (height == 0) {
            MetadataElement currentElement;
            if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_GEOMETRY)) != null)) {
                height = Integer.parseInt(currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_ROWS));
            }
        }
        return height;
    }

    @Override
    public String[] getRasterFileNames() {
        String[] fileNames = null;
        if (this.tiffFiles != null) {
            fileNames = new String[this.tiffFiles.size()];
            fileNames = this.tiffFiles.values().toArray(fileNames);
        }
        return fileNames;
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return getProductName();
    }

    /**
     * Reads the date of the acquisition of the image, as it appears in the metadata file, under the tag METADATA/HEADER/DATE_PDV
     *
     * @return the UTC date representing the date of the acquisition of the image, as it appears in the metadata file
     */
    public ProductData.UTC getDatePdv() {
        String dateStr = null;
        ProductData.UTC dateValue = null;
        MetadataElement currentElement;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_HEADER)) != null)) {
            dateStr = currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_DATE_PDV);
        }
        if (dateStr != null) {
            dateValue = DateHelper.parseDate(dateStr, SpotConstants.SPOT4_TAKE5_UTC_DATE_FORMAT);
        }
        return dateValue;
    }

    /**
     * Reads the geographic zone of the image, as it appears in the metadata file, under the tag METADATA/HEADER/ZONE_GEO
     *
     * @return the geographic zone of the image, as it appears in the metadata file
     */
    public String getGeographicZone() {
        MetadataElement currentElement;
        String result = null;
        if (((currentElement = rootElement.getElement(SpotConstants.SPOT4_TAKE5_TAG_HEADER)) != null)) {
            result = currentElement.getAttributeString(SpotConstants.SPOT4_TAKE5_TAG_ZONE_GEO);
        }
        return result;
    }
}

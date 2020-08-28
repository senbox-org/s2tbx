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

package org.esa.s2tbx.dataio.pleiades.dimap;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a DIMAP volume metadata file, which points to individual components (products).
 * As of now, Pleiades scene products have only one component.
 *
 * @author Cosmin Cara
 */
public class VolumeMetadata extends GenericXmlMetadata {

    final List<VolumeComponent> components;
    List<GenericXmlMetadata> componentMetadata;

    static class VolumeMetadataParser extends XmlMetadataParser<VolumeMetadata> {

        public VolumeMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected ProductData inferType(String elementName, String value) {
            return ProductData.createInstance(value);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    public static VolumeMetadata create(Path path) throws IOException {
        Assert.notNull(path);
        try (InputStream inputStream = Files.newInputStream(path);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             FilePathInputStream filePathInputStream = new FilePathInputStream(path, bufferedInputStream, null)) {

            return create(filePathInputStream);
        }
    }

    public static VolumeMetadata create(FilePathInputStream filePathInputStream) throws IOException {
        VolumeMetadata result = null;
        try {
            VolumeMetadataParser parser = new VolumeMetadataParser(VolumeMetadata.class);
            result = parser.parse(filePathInputStream);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException(e);
        }
        Path path = filePathInputStream.getPath();
        result.setPath(path);
        result.setFileName(path.getFileName().toString());
        String[] titles = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TITLE);
        if (titles == null) {
            titles = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TITLE_ALT);
        }
        if (titles != null && titles.length > 0) {
            String[] types = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TYPE);
            if (types == null) {
                types = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TYPE_ALT);
            }
            String[] paths = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_PATH);
            if (paths == null) {
                paths = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_PATH_ALT);
            }
            String[] tnPaths = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TN_PATH);
            if (tnPaths == null) {
                tnPaths = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TN_PATH_ALT);
            }
            String[] tnFormats = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TN_FORMAT);
            if (tnFormats == null) {
                tnFormats = result.getAttributeValues(Constants.PATH_VOL_COMPONENT_TN_FORMAT_ALT);
            }

            Path parentFolderPath = path.getParent();

            for (int i = 0; i < titles.length; i++) {
                VolumeComponent component = new VolumeComponent(parentFolderPath);
                component.setTitle(titles[i]);
                if (types != null && types.length == titles.length) {
                    component.setType(types[i]);
                }
                if (paths != null && paths.length == titles.length) {
                    Path child = null;
                    try {
                        child = parentFolderPath.resolve(paths[i]);
                    } catch (InvalidPathException ignored) {
                        // ignore
                    }
                    if (child != null && Files.exists(child)) {
                        component.setRelativePath(paths[i]);
                    }
                }
                if (tnPaths != null && tnPaths.length == titles.length) {
                    component.setThumbnailPath(tnPaths[i]);
                }
                if (tnFormats != null && tnFormats.length == titles.length) {
                    component.setThumbnailFormat(tnFormats[i]);
                }
                result.components.add(component);
            }
            result.componentMetadata = result.getNextLevelMetadata();
        }
        return result;
    }

    public VolumeMetadata(String name) {
        super(name);
        components = new ArrayList<>();
        componentMetadata = new ArrayList<>();
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return getAttributeValue(Constants.PATH_VOL_METADATA_PROFILE, Constants.METADATA_FORMAT);
    }

    public List<VolumeComponent> getComponents() {
        return components;
    }

    public List<VolumeComponent> getDimapComponents() {
        return this.components.stream().filter(current -> current.getType().equals(Constants.METADATA_FORMAT)).collect(Collectors.toList());
    }

    public List<VolumeMetadata> getVolumeMetadataList() {
        return componentMetadata.stream().filter(metadata -> metadata instanceof VolumeMetadata).map(metadata -> (VolumeMetadata) metadata).collect(Collectors.toList());
    }

    public List<ImageMetadata> getImageMetadataList() {
        return componentMetadata.stream().filter(metadata -> metadata instanceof ImageMetadata).map(metadata -> (ImageMetadata) metadata).collect(Collectors.toList());
    }

    public int getSceneWidth() {
        return componentMetadata.stream()
                                .filter(metadata -> metadata instanceof ImageMetadata)
                                .map(metadata -> (ImageMetadata) metadata)
                                .map(ImageMetadata::getRasterWidth)
                                .collect(Collectors.maxBy(Integer::compare))
                                .get();
    }

    public int getSceneHeight() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .map(ImageMetadata::getRasterHeight)
                .collect(Collectors.maxBy(Integer::compare))
                .get();
    }

    public ImageMetadata getMaxResolutionImage() {
        return componentMetadata.stream()
                .filter(metadata -> metadata instanceof ImageMetadata)
                .map(metadata -> (ImageMetadata) metadata)
                .collect(Collectors.maxBy(Comparator.comparingInt(ImageMetadata::getRasterWidth)))
                .get();
    }

    public String getFormat() {
        return getAttributeValue(Constants.PATH_VOL_METADATA_FORMAT, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getFormatVersion() {
        return getAttributeValue(Constants.PATH_VOL_METADATA_FORMAT_VERSION, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getSubProfile() {
        return getAttributeValue(Constants.PATH_VOL_METADATA_SUBPROFILE, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getLanguage() {
        return getAttributeValue(Constants.PATH_VOL_METADATA_LANGUAGE, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getDatasetName() {
        return getAttributeValue(Constants.PATH_VOL_DATASET_NAME, Constants.VALUE_NOT_AVAILABLE);
    }

    public Integer getDatasetId() {
        return Integer.parseInt(getAttributeValue(Constants.PATH_VOL_DATASET_ID, Constants.STRING_ONE));
    }

    public String getProducerName() {
        return getAttributeValue(Constants.PATH_VOL_PRODUCER_NAME, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getProducerURL() {
        return getAttributeValue(Constants.PATH_VOL_PRODUCER_URL, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getProducerContact() {
        return getAttributeValue(Constants.PATH_VOL_PRODUCER_CONTACT, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getProducerAddress() {
        return getAttributeValue(Constants.PATH_VOL_PRODUCER_ADDRESS, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getDistributorName() {
        return getAttributeValue(Constants.PATH_VOL_DISTRIBUTOR_NAME, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getDistributorURL() {
        return getAttributeValue(Constants.PATH_VOL_DISTRIBUTOR_URL, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getDistributorContact() {
        return getAttributeValue(Constants.PATH_VOL_DISTRIBUTOR_CONTACT, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getDistributorAddress() {
        return getAttributeValue(Constants.PATH_VOL_DISTRIBUTOR_ADDRESS, Constants.VALUE_NOT_AVAILABLE);
    }

    public ProductData.UTC getProductionDate() {
        ProductData.UTC prodDate = null;
        String stringData = getAttributeValue(Constants.PATH_VOL_PRODUCTION_DATE, null);
        if (stringData != null && !stringData.isEmpty()) {
            prodDate = DateHelper.parseDate(stringData, Constants.UTC_DATE_FORMAT);
        }
        return prodDate;
    }

    public String getProductType() {
        String type = getAttributeValue(Constants.PATH_VOL_PRODUCT_TYPE, Constants.PRODUCT);
        if (Constants.VALUE_NOT_AVAILABLE.equals(type)) {
            type = this.components.get(0).getType();
        }
        return type;
    }

    public String getProductCode() {
        return getAttributeValue(Constants.PATH_VOL_PRODUCT_CODE, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getProductInfo() {
        return getAttributeValue(Constants.PATH_VOL_PRODUCT_INFO, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getJobId() {
        return getAttributeValue(Constants.PATH_VOL_JOB_ID, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getCustomerReference() {
        return getAttributeValue(Constants.PATH_VOL_CUSTOMER_REFERENCE, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getInternalReference() {
        String ref = getAttributeValue(Constants.PATH_VOL_INTERNAL_REFERENCE, Constants.VALUE_NOT_AVAILABLE);
        if (Constants.VALUE_NOT_AVAILABLE.equals(ref)) {
            ref = this.components.get(0).getTitle();
        }
        return ref;
    }

    public String getCommercialReference() {
        return getAttributeValue(Constants.PATH_VOL_COMMERCIAL_REFERENCE, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getCommercialItem() {
        return getAttributeValue(Constants.PATH_VOL_COMMERCIAL_ITEM, Constants.VALUE_NOT_AVAILABLE);
    }

    public String getComment() {
        return getAttributeValue(Constants.PATH_VOL_COMMENT, Constants.VALUE_NOT_AVAILABLE);
    }

    private List<GenericXmlMetadata> getNextLevelMetadata() {
        List<GenericXmlMetadata> subComponents = new ArrayList<>();
        List<VolumeComponent> dimapComponents = getDimapComponents();
        for (VolumeComponent component : dimapComponents) {
            GenericXmlMetadata componentMetadata = component.getComponentMetadata();
            if (componentMetadata instanceof VolumeMetadata) {
                subComponents.addAll(((VolumeMetadata)componentMetadata).getNextLevelMetadata());
            }
            subComponents.add(componentMetadata);
        }
        return subComponents;
    }
}

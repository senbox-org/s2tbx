package org.esa.beam.dataio.rapideye.nitf;

import nitf.*;
import nitf.imageio.NITFReader;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for reading NITF file metadata. Descriptions are taken from MIL-STD-2500C document.
 *
 * @author  Cosmin Cara
 */
public class NITFMetadataAdapter {

    private static Map<String, FieldDescriptor> fieldMap;
    private static void addMapEntry(String name, String description) {
        addMapEntry(name, description, ProductData.TYPE_ASCII);
    }

    private static void addMapEntry(String name, String description, int dataType) {
        fieldMap.put(name, new FieldDescriptor(description, dataType));
    }

    static {
        fieldMap = new HashMap<String, FieldDescriptor>();
        addMapEntry(NITFFields.FHDR, "File profile name");
        addMapEntry(NITFFields.FVER, "File version");
        addMapEntry(NITFFields.CLEVEL, "Complexity level");
        addMapEntry(NITFFields.STYPE, "Standard type");
        addMapEntry(NITFFields.OSTAID, "Originating station ID");
        addMapEntry(NITFFields.FDT, "File date and time");
        addMapEntry(NITFFields.FTITLE, "File title");
        addMapEntry(NITFFields.FSCLAS, "File security classification");
        addMapEntry(NITFFields.FSCLSY, "File classification security system");
        addMapEntry(NITFFields.FSCOP, "File copy number");
        addMapEntry(NITFFields.ENCRYP, "Encryption");
        addMapEntry(NITFFields.FBKGC, "File background color");
        addMapEntry(NITFFields.ONAME, "Originator's name");
        addMapEntry(NITFFields.OPHONE, "Originator's phone number");
        addMapEntry(NITFFields.FL,"File length");
        addMapEntry(NITFFields.HL, "NITF file header length");
        addMapEntry(NITFFields.NUMI,"Number of image segments");
        addMapEntry(NITFFields.UDHDL, "User defined header data length");
        addMapEntry(NITFFields.UDHOFL, "User defined header overflow");
        addMapEntry(NITFFields.UDHD, "User defined header data");
        addMapEntry(NITFFields.XHDL, "Extended header data length");
        addMapEntry(NITFFields.XHDLOFL,"Extended header data overflow");
        addMapEntry(NITFFields.XHD, "Extended header data");
        // image subheader fields
        addMapEntry(NITFFields.IM, "File part type");
        addMapEntry(NITFFields.IID_1,"Image identifier 1");
        addMapEntry(NITFFields.IDATIM,"Image date and time");
        addMapEntry(NITFFields.TGTID, "Target identifier");
        addMapEntry(NITFFields.ISCLAS, "Image security classification");
        addMapEntry(NITFFields.ISORCE, "Image source");
        addMapEntry(NITFFields.NROWS, "Number of significant rows in image");
        addMapEntry(NITFFields.NCOLS, "Number of significant columns in image");
        addMapEntry(NITFFields.PVTYPE, "Pixel value type");
        addMapEntry(NITFFields.IREP, "Image representation");
        addMapEntry(NITFFields.ICAT, "Image category");
        addMapEntry(NITFFields.ABPP, "Actual bits-per-pixel per band");
        addMapEntry(NITFFields.PJUST, "Pixel justification");
        addMapEntry(NITFFields.ICORDS, "Image coordinate representation");
        addMapEntry(NITFFields.IGEOLO, "Image geographic location");
        addMapEntry(NITFFields.NICOM, "Number of image comments");
        addMapEntry(NITFFields.IC, "Image compression");
        addMapEntry(NITFFields.NBANDS, "Number of bands");
        addMapEntry(NITFFields.XBANDS, "Number of multispectral bands");
        addMapEntry(NITFFields.IREPBAND, "Band representation");
        addMapEntry(NITFFields.ISUBCAT, "Band subcategory");
        addMapEntry(NITFFields.IFC, "Band image filter condition");
        addMapEntry(NITFFields.IMFLT, "Band standard image filter code");
        addMapEntry(NITFFields.NLUTS, "Number of LUTs for the image band");
        addMapEntry(NITFFields.NELUT, "Number of LUT entries for the image band");
        addMapEntry(NITFFields.ISYNC, "Image sync code");
        addMapEntry(NITFFields.IMODE, "Image mode");
        addMapEntry(NITFFields.NBPR, "Number of blocks per row", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.NBPC, "Number of blocks per column", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.NPPBH, "Number of pixels per block horizontal", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.NPPBV, "Number of pixels per block vertical", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.NBPP, "Number of bits-per-pixel per band", ProductData.TYPE_UINT8);
        addMapEntry(NITFFields.IDLVL, "Image display level", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.IALVL, "Attachment level", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.ILOC, "Image location");
        addMapEntry(NITFFields.IMAG, "Image magnification", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.UDIDL, "User defined image data length", ProductData.TYPE_UINT32);
        addMapEntry(NITFFields.UDOFL, "User defined overflow", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.IXSHDL, "Image extended subheader data length", ProductData.TYPE_UINT32);
        addMapEntry(NITFFields.IXSOFL, "Image extended subheader overflow", ProductData.TYPE_UINT16);
        addMapEntry(NITFFields.NUMDES, "Number of data extension segments", ProductData.TYPE_UINT8);
        addMapEntry(NITFFields.NUMRES, "Number of reserved extension segments", ProductData.TYPE_UINT8);
    }

    private static MetadataElement readHeader(FileHeader header) {
        MetadataElement headerElement = new MetadataElement("File Header");
        headerElement.addAttribute(asMetadataAttribute(header.getFileHeader(), NITFFields.FHDR));
        headerElement.addAttribute(asMetadataAttribute(header.getFileVersion(), NITFFields.FVER));
        headerElement.addAttribute(asMetadataAttribute(header.getComplianceLevel(), NITFFields.CLEVEL));
        headerElement.addAttribute(asMetadataAttribute(header.getSystemType(), NITFFields.STYPE));
        headerElement.addAttribute(asMetadataAttribute(header.getOriginStationID(), NITFFields.OSTAID));
        headerElement.addAttribute(asMetadataAttribute(header.getFileDateTime(), NITFFields.FDT));
        headerElement.addAttribute(asMetadataAttribute(header.getFileTitle(), NITFFields.FTITLE));
        headerElement.addAttribute(asMetadataAttribute(header.getClassification(), NITFFields.FSCLAS));
        headerElement.addAttribute(asMetadataAttribute(header.getMessageCopyNum(), NITFFields.FSCOP));
        headerElement.addAttribute(asMetadataAttribute(header.getEncrypted(), NITFFields.ENCRYP));
        headerElement.addAttribute(asMetadataAttribute(header.getBackgroundColor(), NITFFields.FBKGC));
        headerElement.addAttribute(asMetadataAttribute(header.getOriginatorName(), NITFFields.ONAME));
        headerElement.addAttribute(asMetadataAttribute(header.getOriginatorPhone(), NITFFields.OPHONE));
        headerElement.addAttribute(asMetadataAttribute(header.getFileLength(), NITFFields.FL));
        headerElement.addAttribute(asMetadataAttribute(header.getHeaderLength(), NITFFields.HL));
        headerElement.addAttribute(asMetadataAttribute(header.getUserDefinedHeaderLength(), NITFFields.UDHDL));
        headerElement.addAttribute(asMetadataAttribute(header.getExtendedHeaderLength(), NITFFields.XHDL));
        headerElement.addAttribute(asMetadataAttribute(header.getNumImages(), NITFFields.NUMI));
        headerElement.addAttribute(asMetadataAttribute(header.getNumDataExtensions(), NITFFields.NUMDES));
        headerElement.addAttribute(asMetadataAttribute(header.getNumReservedExtensions(), NITFFields.NUMRES));
        return headerElement;
    }

    private static MetadataElement readImageSubheaders(ImageSegment[] imageSegments) {
        MetadataElement subheaderCol = new MetadataElement("Image Subheaders");
        for (int i = 0; i < imageSegments.length; i++) {
            ImageSubheader subheader = imageSegments[i].getSubheader();
            MetadataElement imageSubheaderElem = new MetadataElement("Image Subheader " + String.valueOf(i));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getFilePartType(), NITFFields.IM));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageId(), NITFFields.IID_1));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageDateAndTime(), NITFFields.IDATIM));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getTargetId(), NITFFields.TGTID));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageTitle(), "ImageTitle"));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageSecurityClass(), NITFFields.ISCLAS));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getEncrypted(), NITFFields.ENCRYP));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageSource(), NITFFields.ISORCE));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumRows(), NITFFields.NROWS));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumCols(), NITFFields.NCOLS));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getPixelValueType(), "PVTYPE"));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageRepresentation(), NITFFields.IREP));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageCategory(), NITFFields.ICAT));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getActualBitsPerPixel(), NITFFields.ABPP));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getPixelJustification(), NITFFields.PJUST));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageCoordinateSystem(), NITFFields.ICORDS));

            imageSubheaderElem.addElement(readCornerCoordinates(subheader.getCornerCoordinates().getStringData()));

            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageCompression(), NITFFields.IC));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumImageBands(), NITFFields.NBANDS));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumMultispectralImageBands(), NITFFields.XBANDS));

            BandInfo[] bandInfos = subheader.getBandInfo();
            MetadataElement biElem = new MetadataElement("Bands");
            for (int j = 0; j < bandInfos.length; j++) {
                MetadataElement bandInfoElem = new MetadataElement("BAND" + (j+1));
                bandInfoElem.addAttribute(asMetadataAttribute(bandInfos[i].getRepresentation(), NITFFields.IREPBAND));
                bandInfoElem.addAttribute(asMetadataAttribute(bandInfos[i].getSubcategory(), NITFFields.ISUBCAT));
                bandInfoElem.addAttribute(asMetadataAttribute(bandInfos[i].getImageFilterCode(), NITFFields.IFC));
                bandInfoElem.addAttribute(asMetadataAttribute(bandInfos[i].getImageFilterCondition(), NITFFields.IMFLT));
                bandInfoElem.addAttribute(asMetadataAttribute(bandInfos[i].getNumLUTs(), NITFFields.NLUTS));
                bandInfoElem.addAttribute(asMetadataAttribute(bandInfos[i].getBandEntriesPerLUT(), NITFFields.NELUT));
                biElem.addElement(bandInfoElem);
            }
            imageSubheaderElem.addElement(biElem);

            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageSyncCode(), NITFFields.ISYNC));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageMode(), NITFFields.IMODE));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumBlocksPerRow(), NITFFields.NBPR));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumBlocksPerCol(), NITFFields.NBPC));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumPixelsPerHorizBlock(), NITFFields.NPPBH));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumPixelsPerVertBlock(), NITFFields.NPPBV));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getNumBitsPerPixel(), NITFFields.NBPP));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageDisplayLevel(), NITFFields.IDLVL));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageAttachmentLevel(), NITFFields.IALVL));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageLocation(), NITFFields.ILOC));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getImageMagnification(), NITFFields.IMAG));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getUserDefinedImageDataLength(), NITFFields.UDIDL));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getUserDefinedOverflow(), NITFFields.UDOFL));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getExtendedHeaderLength(), NITFFields.IXSHDL));
            imageSubheaderElem.addAttribute(asMetadataAttribute(subheader.getExtendedHeaderOverflow(), NITFFields.IXSOFL));

            subheaderCol.addElement(imageSubheaderElem);
        }
        return subheaderCol;
    }

    private static MetadataElement readCornerCoordinates(String coordString) {
        MetadataElement cornerCoordElem = new MetadataElement(NITFFields.IGEOLO);
        cornerCoordElem.setDescription(fieldMap.get(NITFFields.IGEOLO).getDescription());
        if (coordString != null && !coordString.isEmpty()) {
            String[] tokens = coordString.split("E|V");
            for (int j = 0; j < tokens.length; j++) {
                MetadataElement cornerElem;
                switch (j) {
                    case 0:
                        cornerElem = new MetadataElement("topLeft");
                        break;
                    case 1:
                        cornerElem = new MetadataElement("topRight");
                        break;
                    case 2:
                        cornerElem = new MetadataElement("bottomRight");
                        break;
                    case 3:
                        cornerElem = new MetadataElement("bottomLeft");
                        break;
                    default:
                        cornerElem = new MetadataElement("Corner");
                }
                String[] coords = tokens[j].split("N|S");
                MetadataAttribute latAttr = new MetadataAttribute("latitude",
                                                                  ProductData.ASCII.createInstance(coords[0].substring(0, 2) + "*" + coords[0].substring(2, 4) + "'" + coords[0].substring(4) + "''"),
                                                                                                   false);
                cornerElem.addAttribute(latAttr);
                MetadataAttribute longAttr = new MetadataAttribute("longitude",
                                                                   ProductData.ASCII.createInstance(coords[1].substring(0, 3) + "*" + coords[1].substring(3, 5) + "'" + coords[1].substring(5) + "''"),
                                                                                                   false);
                cornerElem.addAttribute(longAttr);
                cornerCoordElem.addElement(cornerElem);
            }
        }
        return cornerCoordElem;
    }

    private static MetadataElement readDataExtensions(DESegment[] segments) throws NITFException {
        MetadataElement dataExtensions = new MetadataElement("DES");
        if (segments != null) {
            for (DESegment segment : segments) {
                DESubheader header = segment.getSubheader();
                if (header != null) {
                    MetadataElement deElem = new MetadataElement("DE");
                    TRE subheaderFields = header.getSubheaderFields();
                    if (subheaderFields != null) {
                        TRE.TREIterator iterator = subheaderFields.iterator();
                        while (iterator.hasNext()) {
                            TRE.FieldPair pair = iterator.next();
                            deElem.addAttribute(asMetadataAttribute(pair.getField(), pair.getName()));
                        }
                    }
                    dataExtensions.addElement(deElem);
                }
            }
        }
        return dataExtensions;
    }

    public static MetadataElement read(NITFReader reader) throws IOException, NITFException {
        MetadataElement root = new MetadataElement("NITF Metadata");
        Record record = reader.getRecord();
        // File header
        FileHeader header = record.getHeader();
        root.addElement(readHeader(header));
        // Image subheaders
        ImageSegment[] imageSegments = record.getImages();
        root.addElement(readImageSubheaders(imageSegments));
        // RPC00B TRE
        DESegment[] dataExtensions = record.getDataExtensions();
        root.addElement(readDataExtensions(dataExtensions));
        return root;
    }

    private static MetadataAttribute asMetadataAttribute(Field field, String name) {
        MetadataAttribute attribute;
        if (field != null) {
            attribute = new MetadataAttribute(name, ProductData.ASCII.createInstance(field.toString().trim()), false);
        } else {
            attribute = new MetadataAttribute(name, ProductData.TYPE_ASCII);
        }
        if (fieldMap.containsKey(name)) {
            attribute.setDescription(fieldMap.get(name).getDescription());
        }
        return attribute;
    }

    private static class FieldDescriptor {
        public int getDataType() {
            return dataType;
        }

        private int dataType;

        public String getDescription() {
            return description;
        }

        private String description;

        FieldDescriptor(String description, int dataType) {
            this.description = description;
            this.dataType = dataType;
        }
    }

}

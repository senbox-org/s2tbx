package org.esa.s2tbx.dataio.worldview2.internal;

import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.dataio.XmlMetadataInspector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldView2MetadataInspector extends XmlMetadataInspector {

    /**
     * Parses the metadata for the given product path
     *
     * @param productPath
     */
    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        List<Path> metadataFilesList = new ArrayList<>();
        if (!Files.exists(productPath)) {
            return null;
        } else if (productPath.toString().endsWith("_README.XML")) {
            //get all the .XML files declare in the _README.XML file
            try {
                readDocument(productPath);
            } catch (Exception e) {
                logger.warning(String.format("Cannot read metadata %s. Reason: %s", productPath.getFileName(), e.getMessage()));
                throw new IOException(e);
            }
            Set<String> fileList = getValues("/README/FILELIST/FILE/text()");
            for (String file : fileList) {
                if (file.endsWith(".XML") && !file.equalsIgnoreCase(productPath.getFileName().toString())) {
                    metadataFilesList.add(productPath.getParent().resolve(file));
                }
            }
        }
        //if the _README.XML file contains more metadata files get information from them
        Metadata metadata = new Metadata();
        if (!metadataFilesList.isEmpty()) {
            for (Path path : metadataFilesList) {
                try {
                    readDocument(path);
                } catch (Exception e) {
                    logger.warning(String.format("Cannot read metadata %s. Reason: %s", path.getFileName(), e.getMessage()));
                    throw new IOException(e);
                }
                String bandId = getValue("/isd/IMD/BANDID/text()");
                if (bandId.equals("MS1") || bandId.equals("Multi")) {
                    for (String bandConstant : WorldView2Constants.BAND_NAMES_MULTISPECTRAL_8_BANDS) {
                        if (!bandConstant.contains(" ") && !bandConstant.contains("2") && existElement("BAND_" + bandConstant.substring(0, 1))) {
                            metadata.getBandList().add(bandConstant);
                        } else if (bandConstant.contains(" ") && existElement("BAND_RE")) {
                            metadata.getBandList().add(bandConstant);
                        } else if (bandConstant.contains("2") && existElement("BAND_N2")) {
                            metadata.getBandList().add(bandConstant);
                        }
                    }
                } else if (existElement("BAND_P")) {
                    metadata.getBandList().add("Pan");
                }
            }
        }
        metadata.setHasMasks(false);
        return metadata;
    }
}

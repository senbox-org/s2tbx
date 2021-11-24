package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SceneDescription;
import org.esa.s2tbx.dataio.s2.S2SpectralInformation;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.gml.EopPolygon;
import org.esa.s2tbx.dataio.s2.masks.MaskInfo;
import org.esa.snap.runtime.Config;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * @author Denisa Stefanescu
 */
public class S2OrthoUtils {

    protected static Logger logger = Logger.getLogger(S2OrthoUtils.class.getName());

    public static DefaultFeatureCollection createDefaultFeatureCollection(List<EopPolygon>[] productPolygons, int maskInfoIndex, SimpleFeatureType type){
        final DefaultFeatureCollection collection = new DefaultFeatureCollection("S2L1CMasks", type);
        for (int index = 0; index < productPolygons[maskInfoIndex].size(); index++) {
            Polygon polygon = productPolygons[maskInfoIndex].get(index).getPolygon();

            Object[] data1 = {polygon, String.format("%s[%s]", productPolygons[maskInfoIndex].get(index).getId(), index)};
            SimpleFeatureImpl f1 = new SimpleFeatureImpl(data1, type, new FeatureIdImpl(String.format("%s[%s]", productPolygons[maskInfoIndex].get(index).getId(), index)), true);
            collection.add(f1);
        }
        return collection;
    }

    public static List<String> createDistictPolygonsOrdered(SimpleFeatureIterator simpleFeatureIterator){
        Set<String> distictPolygons = new HashSet<>();
        while (simpleFeatureIterator.hasNext()) {
            SimpleFeature simpleFeature = simpleFeatureIterator.next();
            if (simpleFeature.getID().contains("-")) {
                distictPolygons.add(simpleFeature.getID().substring(0, simpleFeature.getID().lastIndexOf("-")));
            }
        }
        simpleFeatureIterator.close();
        return S2SceneDescription.asSortedList(distictPolygons);
    }

    public static boolean foundMaskFiles(List<S2Metadata.Tile> tileList, MaskInfo maskInfo, S2SpectralInformation spectralInfo) {
        boolean maskFilesFound = false;
        for (S2Metadata.Tile tile : tileList) {

            if (tile.getMaskFilenames() == null) {
                continue;
            }

            for (S2Metadata.MaskFilename maskFilename : tile.getMaskFilenames()) {

                // We are only interested in a single mask main type
                if (!maskFilename.getType().equals(maskInfo.getMainType())) {
                    continue;
                }

                if (spectralInfo != null) {
                    // We are only interested in masks for a certain band
                    if (!maskFilename.getBandId().equals(String.format("%s", spectralInfo.getBandId()))) {
                        continue;
                    }
                }

                maskFilesFound = true;
            }
        }
        return maskFilesFound;
    }

    public static List<EopPolygon> readPolygons(VirtualPath path) {
        List<EopPolygon> polygonsForTile = new ArrayList<>();
        String line;
        String polygonWKT;
        String type = "";
        String lastId = "id";

        WKTReader wkt = new WKTReader();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(path.getInputStream()))) {
            try {
                while ((line = in.readLine()) != null) {
                    if (line.contains("eop:MaskFeature gml:id=")) {
                        lastId = line.substring(line.indexOf("=\"") + 2, line.indexOf("\">"));
                    } else if (line.contains("</eop:maskType>")) {
                        type = line.substring(line.indexOf(">") + 1, line.indexOf("</eop:maskType>"));
                    } else if (line.contains("<gml:posList srsDimension")) {
                        String polygon = line.substring(line.indexOf(">") + 1, line.indexOf("</gml:posList>"));
                        polygonWKT = convertToWKTPolygon(polygon, readPolygonDimension(line));
                        EopPolygon polyg = new EopPolygon(lastId, type, (Polygon) wkt.read(polygonWKT));
                        polygonsForTile.add(polyg);
                        lastId = "id"; //re-initialize lastId
                    }
                }
            } catch (Exception e) {
                logger.warning(String.format("Warning: missing polygon in mask %s\n", path.getFileName().toString()));
            }
        } catch (Exception e) {
            logger.warning(String.format("Warning: impossible to read properly %s\n", path.getFullPathString()));
        }

        return polygonsForTile;
    }

    private static String convertToWKTPolygon(String line, int dimension) throws IOException {

        if (dimension <= 0) throw new IOException("Invalid dimension");

        StringBuilder output = new StringBuilder("POLYGON((");

        int pos = 0;
        int end;
        int count = 0;
        while ((end = line.indexOf(' ', pos)) >= 0) {
            output = output.append(line.substring(pos, end));
            pos = end + 1;
            count++;
            if (count == dimension) {
                output = output.append(",");
                count = 0;
            } else {
                output = output.append(" ");
            }
        }
        //add last coordinate
        output = output.append(line.substring(line.lastIndexOf(' ') + 1));
        output = output.append("))");
        return output.toString();
    }

    private static int readPolygonDimension(String line) {
        String label = "srsDimension=\"";
        int position = line.indexOf(label);
        if (position == -1) {
            return 0;
        }
        try {
            return Integer.parseInt(line.substring(position + label.length(), position + label.length() + 1));
        } catch (Exception e) {
            return 0;
        }
    }

    static public boolean addNegativeOffset() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        final boolean DEFAULT_MASK_ENABLEMENT = true;
        return preferences.getBoolean("s2tbx.dataio.negativeRadiometricOffset", DEFAULT_MASK_ENABLEMENT);
    }

    static public boolean enableECMWFTData() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        final boolean DEFAULT_MASK_ENABLEMENT = true;
        return preferences.getBoolean("s2tbx.dataio.ECMWFTData", DEFAULT_MASK_ENABLEMENT);
    }

    static public boolean enableCAMSData() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        final boolean DEFAULT_MASK_ENABLEMENT = true;
        return preferences.getBoolean("s2tbx.dataio.CAMSData", DEFAULT_MASK_ENABLEMENT);
    }
}

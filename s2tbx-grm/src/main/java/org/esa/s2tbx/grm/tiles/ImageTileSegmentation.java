package org.esa.s2tbx.grm.tiles;

import org.esa.s2tbx.dataio.jp2.JP2ProductReaderPlugin;
import org.esa.s2tbx.grm.AbstractSegmenter;
import org.esa.s2tbx.grm.Graph;
import org.esa.s2tbx.grm.Node;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.rgb.ImageProductReaderPlugIn;
import org.esa.snap.core.datamodel.Graticule;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.dataio.geotiff.GeoTiffProductReaderPlugIn;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by jcoravu on 14/3/2017.
 */
public class ImageTileSegmentation {

    public ImageTileSegmentation() {
    }

    public static void main(String args[]) {
        IntSet set = new IntSet();
        set.add(4);
        set.add(1);
        set.add(2);
        System.out.println("size="+set.size());
//        IntToObjectMap<String> borderNodes = new IntToObjectMap<String>();
//        borderNodes.put(2, "2Value");
//        borderNodes.put(1, "1Value");
////        borderNodes.put(3, "3");
//
//        Iterator<IntToObjectMap.Entry<String>> itValues = borderNodes.entriesIterator();
//        while (itValues.hasNext()) {
//            IntToObjectMap.Entry<String> entry = itValues.next();
//            System.out.println("key="+entry.getKey()+"  value="+entry.getValue());
//        }
//
//        Iterator<String> itValues = borderNodes.valuesIterator();
//        while (itValues.hasNext()) {
//            String node = itValues.next();
//            System.out.println("node="+node);
//        }

        try {
//            File file = new File("D:\\GRM\\GTiff-4x4.tif");
//            GeoTiffProductReaderPlugIn readerPlugin = new GeoTiffProductReaderPlugIn();
//            ProductReader productReader = readerPlugin.createReaderInstance();
//            Product product = productReader.readProductNodes(file, null);
//            System.out.println("product="+product);
//
//            float spectralWeight = 0.5f;
//            float shapeWeight = 0.5f;
//            float threshold = 9.0f;
//            int bandIndices[] = new int[] {0};

//            File file = new File("D:\\GRM\\GTiff-20x30.tif");
//            GeoTiffProductReaderPlugIn readerPlugin = new GeoTiffProductReaderPlugIn();
//            ProductReader productReader = readerPlugin.createReaderInstance();
//            Product product = productReader.readProductNodes(file, null);
//            System.out.println("product="+product);
//
//            float spectralWeight = 0.5f;
//            float shapeWeight = 0.5f;
//            float threshold = 50.0f;
//            int numberOfIterations = 75;
//            int numberOfFirstIterations = 1;
//
//
//            int bandIndices[] = new int[] {0};

//            File file = new File("D:\\GRM\\JPEG-1335x1600.jpg");
//            ImageProductReaderPlugIn readerPlugin = new ImageProductReaderPlugIn();
//            ProductReader productReader = readerPlugin.createReaderInstance();
//            Product product = productReader.readProductNodes(file, null);
//            System.out.println("product="+product);
//
//            float spectralWeight = 0.5f;
//            float shapeWeight = 0.5f;
//            float threshold = 2000.0f;
//            int bandIndices[] = new int[] {0, 1, 2};
//            int numberOfIterations = 75;
//            int numberOfFirstIterations = 2;
//            boolean fastSegmentation = false;
//
//            BaatzSchapeTileSegmenter tileSegmenter = new BaatzSchapeTileSegmenter(spectralWeight, shapeWeight, threshold);
//            AbstractSegmenter segmenter = tileSegmenter.runSegmentation(product, bandIndices, numberOfIterations, numberOfFirstIterations, fastSegmentation);
//            System.out.println("final graph.nodecount="+segmenter.getGraph().getNodeCount());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

package org.esa.s2tbx.grm;

import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.dataio.geotiff.GeoTiffProductReaderPlugIn;

import java.io.File;
import java.io.IOException;

/**
 * @author Jean Coravu
 */
public class ForestMappingProcessor {

    public ForestMappingProcessor() {
    }

    public static void main(String args[]) {
        File file = new File("D:\\GRM\\GTiff-5x10.tif");
        GeoTiffProductReaderPlugIn readerPlugin = new GeoTiffProductReaderPlugIn();
        ProductReader productReader = readerPlugin.createReaderInstance();
        try {
            Product product = productReader.readProductNodes(file, null);
//            System.out.println("ForestMappingProcessor band.count="+product.getNumBands()+"  product.width="+product.getSceneRasterWidth()+"  product.height="+product.getSceneRasterHeight());

            //AbstractSegmenter segmenter = new SpringSegmenter(threshold);
            boolean fastSegmentation = false;
            boolean addFourNeighbors = true;
            float threshold = 10.0f;
            float spectralWeight = 0.5f;
            float shapeWeight = 0.5f;
            int bandIndices[] = new int[]{0};
            AbstractSegmenter segmenter = new BaatzSchapeSegmenter(spectralWeight, shapeWeight, threshold);
            Band band = segmenter.update(product, bandIndices, fastSegmentation, addFourNeighbors);
            System.out.println("ForestMappingProcessor finish");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

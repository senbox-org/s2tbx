package org.esa.s2tbx.fcc.intern;

        import java.io.File;
        import java.io.IOException;
        import java.util.*;

        import com.bc.ceres.core.ProgressMonitor;
        import com.bc.ceres.core.SubProgressMonitor;
        import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
        import it.unimi.dsi.fastutil.ints.IntSet;
        import org.esa.s2tbx.grm.GenericRegionMergingOp;
        import org.esa.snap.core.datamodel.Band;
        import org.esa.snap.core.datamodel.Product;
        import org.esa.snap.core.datamodel.ProductNodeGroup;
        import org.esa.snap.core.gpf.GPF;
        import org.esa.snap.core.gpf.Operator;
        import org.esa.snap.core.gpf.OperatorException;
        import org.esa.snap.core.gpf.OperatorSpi;
        import org.esa.snap.core.gpf.annotations.Parameter;
        import org.esa.snap.core.gpf.annotations.SourceProduct;
        import org.esa.snap.core.gpf.annotations.TargetProduct;
        import org.esa.snap.core.gpf.descriptor.OperatorDescriptor;
        import org.esa.snap.core.gpf.descriptor.SourceProductDescriptor;
        import org.esa.snap.core.gpf.descriptor.SourceProductsDescriptor;
        import org.esa.snap.core.gpf.internal.OperatorExecutor;
        import org.esa.snap.core.util.ProductUtils;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
public class BandsExtractor {

    public static void writeProduct(Product outputProduct, File parentFolder, String fileName){
        File file = new File(parentFolder, fileName);
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String formatName = "BEAM-DIMAP";
        GPF.writeProduct(outputProduct, file, formatName, false, false, ProgressMonitor.NULL);
    }
}

package org.esa.s2tbx.dataio.vectorformats;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.vectorformats.common.AbstractImportVectorDataNodeAction;
import org.esa.s2tbx.dataio.vectorformats.kml.KmlUtils;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductNode;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.VectorDataNode;
import org.esa.snap.core.util.io.SnapFileFilter;
import org.esa.snap.rcp.SnapApp;
import org.esa.snap.rcp.actions.vector.VectorDataNodeImporter;
import org.esa.snap.rcp.layermanager.layersrc.shapefile.SLDUtils;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.FeatureTypeStyleImpl;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeatureType;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

@ActionID(category = "File", id = "ImportVectorDataNodeFromKMLAction")
@ActionRegistration(displayName = "#CTL_ImportVectorDataNodeFromKMLActionText", lazy = false)
@ActionReferences({
        @ActionReference(path = "Menu/File/Import/Vector Data", position = 60),
        @ActionReference(path = "Menu/Vector/Import")
})
@NbBundle.Messages({
        "CTL_ImportVectorDataNodeFromKMLActionText=Vector from KML",
        "CTL_ImportVectorDataNodeFromKMLActionDescription=Import Vector Data Node from KML Schema",
        "CTL_ImportVectorDataNodeFromKMLActionHelp=importKML"
})
public class ImportVectorDataNodeFromKMLAction extends AbstractImportVectorDataNodeAction implements ContextAwareAction, LookupListener {
    private Lookup lookup;
    private final Lookup.Result<Product> result;
    private VectorDataNodeImporter importer;
    private static final String vector_data_type = "KML";

    public ImportVectorDataNodeFromKMLAction() {
        this(Utilities.actionsGlobalContext());
    }

    public ImportVectorDataNodeFromKMLAction(Lookup lookup) {
        this.lookup = lookup;
        result = lookup.lookupResult(Product.class);
        result.addLookupListener(
                WeakListeners.create(LookupListener.class, this, result));
        setEnableState();
        setHelpId(Bundle.CTL_ImportVectorDataNodeFromKMLActionHelp());
        putValue(Action.NAME, Bundle.CTL_ImportVectorDataNodeFromKMLActionText());
        putValue(Action.SHORT_DESCRIPTION, Bundle.CTL_ImportVectorDataNodeFromKMLActionDescription());
    }

    @Override
    public Action createContextAwareInstance(Lookup lookup) {
        return new ImportVectorDataNodeFromKMLAction(lookup);
    }

    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        setEnableState();
    }

    private void setEnableState() {
        boolean state = false;
        ProductNode productNode = lookup.lookup(ProductNode.class);
        if (productNode != null) {
            Product product = productNode.getProduct();
            state = product != null && product.getSceneGeoCoding() != null;
        }
        setEnabled(state);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SnapFileFilter filter = new SnapFileFilter(getVectorDataType(),
                new String[]{".kml"}, "Keyhole Markup Language");
        importer = new VectorDataNodeImporter(getHelpId(), filter, new ImportVectorDataNodeFromKMLAction.VdnKMLReader(), "Import KML schema", "kml.io.dir");
        importer.importGeometry(SnapApp.getDefault());
    }

    @Override
    protected String getDialogTitle() {
        return importer.getDialogTitle();
    }

    @Override
    protected String getVectorDataType() {
        return vector_data_type;
    }

    private class VdnKMLReader implements VectorDataNodeImporter.VectorDataNodeReader {

        @Override
        public VectorDataNode readVectorDataNode(File file, Product product, ProgressMonitor pm) throws IOException {

            DefaultFeatureCollection featureCollection = KmlUtils.kmlFile2FeatureCollection(file, product, pm);
            Style[] styles =  KmlUtils.loadSLD(file);
            FeatureTypeStyle[] featureTypeStyle =  KmlUtils.loadStyleFromKmlFile(file);

            ProductNodeGroup<VectorDataNode> vectorDataGroup = product.getVectorDataGroup();
            String name = VectorDataNodeImporter.findUniqueVectorDataNodeName(featureCollection.getSchema().getName().getLocalPart(),
                    vectorDataGroup);
            if (styles.length > 0 || featureTypeStyle.length > 0) {
                SimpleFeatureType featureType = KmlUtils.createStyledFeatureType(featureCollection.getSchema());
                VectorDataNode vectorDataNode = new VectorDataNode(name, featureType);
                DefaultFeatureCollection styledCollection = vectorDataNode.getFeatureCollection();
                String defaultCSS = vectorDataNode.getDefaultStyleCss();
                KmlUtils.applyStyle(styles, featureTypeStyle, defaultCSS, featureCollection, styledCollection);
                return vectorDataNode;
            } else {
                return new VectorDataNode(name, featureCollection);
            }
        }
    }
}
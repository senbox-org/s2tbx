//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_structure package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Level1CTile_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1C_Tile_Structure.xsd", "Level-1C_Tile");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1CTile }
     * 
     */
    public Level1CTile createLevel1CTile() {
        return new Level1CTile();
    }

    /**
     * Create an instance of {@link Level1CTile.IMGDATA }
     * 
     */
    public Level1CTile.IMGDATA createLevel1CTileIMGDATA() {
        return new Level1CTile.IMGDATA();
    }

    /**
     * Create an instance of {@link Level1CTile.Level1CTileMetadataFile }
     * 
     */
    public Level1CTile.Level1CTileMetadataFile createLevel1CTileLevel1CTileMetadataFile() {
        return new Level1CTile.Level1CTileMetadataFile();
    }

    /**
     * Create an instance of {@link Level1CTile.QIDATA }
     * 
     */
    public Level1CTile.QIDATA createLevel1CTileQIDATA() {
        return new Level1CTile.QIDATA();
    }

    /**
     * Create an instance of {@link Level1CTile.AUXDATA }
     * 
     */
    public Level1CTile.AUXDATA createLevel1CTileAUXDATA() {
        return new Level1CTile.AUXDATA();
    }

    /**
     * Create an instance of {@link Level1CTile.InventoryMetadata }
     * 
     */
    public Level1CTile.InventoryMetadata createLevel1CTileInventoryMetadata() {
        return new Level1CTile.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level1CTile.ManifestSafe }
     * 
     */
    public Level1CTile.ManifestSafe createLevel1CTileManifestSafe() {
        return new Level1CTile.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level1CTile.RepInfo }
     * 
     */
    public Level1CTile.RepInfo createLevel1CTileRepInfo() {
        return new Level1CTile.RepInfo();
    }

    /**
     * Create an instance of {@link Level1CTile.IMGDATA.ImageFiles }
     * 
     */
    public Level1CTile.IMGDATA.ImageFiles createLevel1CTileIMGDATAImageFiles() {
        return new Level1CTile.IMGDATA.ImageFiles();
    }

    /**
     * Create an instance of {@link Level1CTile.Level1CTileMetadataFile.GeometricInfo }
     * 
     */
    public Level1CTile.Level1CTileMetadataFile.GeometricInfo createLevel1CTileLevel1CTileMetadataFileGeometricInfo() {
        return new Level1CTile.Level1CTileMetadataFile.GeometricInfo();
    }

    /**
     * Create an instance of {@link Level1CTile.Level1CTileMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level1CTile.Level1CTileMetadataFile.QualityIndicatorsInfo createLevel1CTileLevel1CTileMetadataFileQualityIndicatorsInfo() {
        return new Level1CTile.Level1CTileMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1CTile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1C_Tile_Structure.xsd", name = "Level-1C_Tile")
    public JAXBElement<Level1CTile> createLevel1CTile(Level1CTile value) {
        return new JAXBElement<Level1CTile>(_Level1CTile_QNAME, Level1CTile.class, null, value);
    }

}

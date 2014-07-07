//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1b_granule_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_1b_granule_structure package. 
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

    private final static QName _Level1BGranule_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1B_Granule_Structure.xsd", "Level-1B_Granule");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_1b_granule_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1BGranule }
     * 
     */
    public Level1BGranule createLevel1BGranule() {
        return new Level1BGranule();
    }

    /**
     * Create an instance of {@link Level1BGranule.IMGDATA }
     * 
     */
    public Level1BGranule.IMGDATA createLevel1BGranuleIMGDATA() {
        return new Level1BGranule.IMGDATA();
    }

    /**
     * Create an instance of {@link Level1BGranule.Level1BGranuleMetadataFile }
     * 
     */
    public Level1BGranule.Level1BGranuleMetadataFile createLevel1BGranuleLevel1BGranuleMetadataFile() {
        return new Level1BGranule.Level1BGranuleMetadataFile();
    }

    /**
     * Create an instance of {@link Level1BGranule.QIDATA }
     * 
     */
    public Level1BGranule.QIDATA createLevel1BGranuleQIDATA() {
        return new Level1BGranule.QIDATA();
    }

    /**
     * Create an instance of {@link Level1BGranule.InventoryMetadata }
     * 
     */
    public Level1BGranule.InventoryMetadata createLevel1BGranuleInventoryMetadata() {
        return new Level1BGranule.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level1BGranule.ManifestSafe }
     * 
     */
    public Level1BGranule.ManifestSafe createLevel1BGranuleManifestSafe() {
        return new Level1BGranule.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level1BGranule.RepInfo }
     * 
     */
    public Level1BGranule.RepInfo createLevel1BGranuleRepInfo() {
        return new Level1BGranule.RepInfo();
    }

    /**
     * Create an instance of {@link Level1BGranule.IMGDATA.ImageFiles }
     * 
     */
    public Level1BGranule.IMGDATA.ImageFiles createLevel1BGranuleIMGDATAImageFiles() {
        return new Level1BGranule.IMGDATA.ImageFiles();
    }

    /**
     * Create an instance of {@link Level1BGranule.Level1BGranuleMetadataFile.GeometricInfo }
     * 
     */
    public Level1BGranule.Level1BGranuleMetadataFile.GeometricInfo createLevel1BGranuleLevel1BGranuleMetadataFileGeometricInfo() {
        return new Level1BGranule.Level1BGranuleMetadataFile.GeometricInfo();
    }

    /**
     * Create an instance of {@link Level1BGranule.Level1BGranuleMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level1BGranule.Level1BGranuleMetadataFile.QualityIndicatorsInfo createLevel1BGranuleLevel1BGranuleMetadataFileQualityIndicatorsInfo() {
        return new Level1BGranule.Level1BGranuleMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1BGranule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1B_Granule_Structure.xsd", name = "Level-1B_Granule")
    public JAXBElement<Level1BGranule> createLevel1BGranule(Level1BGranule value) {
        return new JAXBElement<Level1BGranule>(_Level1BGranule_QNAME, Level1BGranule.class, null, value);
    }

}

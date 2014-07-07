//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1a_granule_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_1a_granule_structure package. 
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

    private final static QName _Level1AGranule_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1A_Granule_Structure.xsd", "Level-1A_Granule");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_1a_granule_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1AGranule }
     * 
     */
    public Level1AGranule createLevel1AGranule() {
        return new Level1AGranule();
    }

    /**
     * Create an instance of {@link Level1AGranule.IMGDATA }
     * 
     */
    public Level1AGranule.IMGDATA createLevel1AGranuleIMGDATA() {
        return new Level1AGranule.IMGDATA();
    }

    /**
     * Create an instance of {@link Level1AGranule.Level1AGranuleMetadataFile }
     * 
     */
    public Level1AGranule.Level1AGranuleMetadataFile createLevel1AGranuleLevel1AGranuleMetadataFile() {
        return new Level1AGranule.Level1AGranuleMetadataFile();
    }

    /**
     * Create an instance of {@link Level1AGranule.InventoryMetadata }
     * 
     */
    public Level1AGranule.InventoryMetadata createLevel1AGranuleInventoryMetadata() {
        return new Level1AGranule.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level1AGranule.QIDATA }
     * 
     */
    public Level1AGranule.QIDATA createLevel1AGranuleQIDATA() {
        return new Level1AGranule.QIDATA();
    }

    /**
     * Create an instance of {@link Level1AGranule.ManifestSafe }
     * 
     */
    public Level1AGranule.ManifestSafe createLevel1AGranuleManifestSafe() {
        return new Level1AGranule.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level1AGranule.RepInfo }
     * 
     */
    public Level1AGranule.RepInfo createLevel1AGranuleRepInfo() {
        return new Level1AGranule.RepInfo();
    }

    /**
     * Create an instance of {@link Level1AGranule.IMGDATA.ImageFiles }
     * 
     */
    public Level1AGranule.IMGDATA.ImageFiles createLevel1AGranuleIMGDATAImageFiles() {
        return new Level1AGranule.IMGDATA.ImageFiles();
    }

    /**
     * Create an instance of {@link Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo }
     * 
     */
    public Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo createLevel1AGranuleLevel1AGranuleMetadataFileGeometricInfo() {
        return new Level1AGranule.Level1AGranuleMetadataFile.GeometricInfo();
    }

    /**
     * Create an instance of {@link Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo createLevel1AGranuleLevel1AGranuleMetadataFileQualityIndicatorsInfo() {
        return new Level1AGranule.Level1AGranuleMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1AGranule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1A_Granule_Structure.xsd", name = "Level-1A_Granule")
    public JAXBElement<Level1AGranule> createLevel1AGranule(Level1AGranule value) {
        return new JAXBElement<Level1AGranule>(_Level1AGranule_QNAME, Level1AGranule.class, null, value);
    }

}

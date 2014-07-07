//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_0_granule_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_0_granule_structure package. 
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

    private final static QName _Level0Granule_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-0_Granule_Structure.xsd", "Level-0_Granule");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_0_granule_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level0Granule }
     * 
     */
    public Level0Granule createLevel0Granule() {
        return new Level0Granule();
    }

    /**
     * Create an instance of {@link Level0Granule.Level0GranuleMetadataFile }
     * 
     */
    public Level0Granule.Level0GranuleMetadataFile createLevel0GranuleLevel0GranuleMetadataFile() {
        return new Level0Granule.Level0GranuleMetadataFile();
    }

    /**
     * Create an instance of {@link Level0Granule.InventoryMetadata }
     * 
     */
    public Level0Granule.InventoryMetadata createLevel0GranuleInventoryMetadata() {
        return new Level0Granule.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level0Granule.IMGDATA }
     * 
     */
    public Level0Granule.IMGDATA createLevel0GranuleIMGDATA() {
        return new Level0Granule.IMGDATA();
    }

    /**
     * Create an instance of {@link Level0Granule.QIDATA }
     * 
     */
    public Level0Granule.QIDATA createLevel0GranuleQIDATA() {
        return new Level0Granule.QIDATA();
    }

    /**
     * Create an instance of {@link Level0Granule.ManifestSafe }
     * 
     */
    public Level0Granule.ManifestSafe createLevel0GranuleManifestSafe() {
        return new Level0Granule.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level0Granule.RepInfo }
     * 
     */
    public Level0Granule.RepInfo createLevel0GranuleRepInfo() {
        return new Level0Granule.RepInfo();
    }

    /**
     * Create an instance of {@link Level0Granule.Level0GranuleMetadataFile.GeometricInfo }
     * 
     */
    public Level0Granule.Level0GranuleMetadataFile.GeometricInfo createLevel0GranuleLevel0GranuleMetadataFileGeometricInfo() {
        return new Level0Granule.Level0GranuleMetadataFile.GeometricInfo();
    }

    /**
     * Create an instance of {@link Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo createLevel0GranuleLevel0GranuleMetadataFileQualityIndicatorsInfo() {
        return new Level0Granule.Level0GranuleMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level0Granule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-0_Granule_Structure.xsd", name = "Level-0_Granule")
    public JAXBElement<Level0Granule> createLevel0Granule(Level0Granule value) {
        return new JAXBElement<Level0Granule>(_Level0Granule_QNAME, Level0Granule.class, null, value);
    }

}

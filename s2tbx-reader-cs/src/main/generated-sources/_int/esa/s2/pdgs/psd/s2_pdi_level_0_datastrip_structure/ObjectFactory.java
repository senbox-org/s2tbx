//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_0_datastrip_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_0_datastrip_structure package. 
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

    private final static QName _Level0Datastrip_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-0_Datastrip_Structure.xsd", "Level-0_Datastrip");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_0_datastrip_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level0Datastrip }
     * 
     */
    public Level0Datastrip createLevel0Datastrip() {
        return new Level0Datastrip();
    }

    /**
     * Create an instance of {@link Level0Datastrip.DataStripMetadataFile }
     * 
     */
    public Level0Datastrip.DataStripMetadataFile createLevel0DatastripDataStripMetadataFile() {
        return new Level0Datastrip.DataStripMetadataFile();
    }

    /**
     * Create an instance of {@link Level0Datastrip.InventoryMetadata }
     * 
     */
    public Level0Datastrip.InventoryMetadata createLevel0DatastripInventoryMetadata() {
        return new Level0Datastrip.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level0Datastrip.QIDATA }
     * 
     */
    public Level0Datastrip.QIDATA createLevel0DatastripQIDATA() {
        return new Level0Datastrip.QIDATA();
    }

    /**
     * Create an instance of {@link Level0Datastrip.ANCDATA }
     * 
     */
    public Level0Datastrip.ANCDATA createLevel0DatastripANCDATA() {
        return new Level0Datastrip.ANCDATA();
    }

    /**
     * Create an instance of {@link Level0Datastrip.ManifestSafe }
     * 
     */
    public Level0Datastrip.ManifestSafe createLevel0DatastripManifestSafe() {
        return new Level0Datastrip.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level0Datastrip.RepInfo }
     * 
     */
    public Level0Datastrip.RepInfo createLevel0DatastripRepInfo() {
        return new Level0Datastrip.RepInfo();
    }

    /**
     * Create an instance of {@link Level0Datastrip.DataStripMetadataFile.GeneralInfo }
     * 
     */
    public Level0Datastrip.DataStripMetadataFile.GeneralInfo createLevel0DatastripDataStripMetadataFileGeneralInfo() {
        return new Level0Datastrip.DataStripMetadataFile.GeneralInfo();
    }

    /**
     * Create an instance of {@link Level0Datastrip.DataStripMetadataFile.ImageDataInfo }
     * 
     */
    public Level0Datastrip.DataStripMetadataFile.ImageDataInfo createLevel0DatastripDataStripMetadataFileImageDataInfo() {
        return new Level0Datastrip.DataStripMetadataFile.ImageDataInfo();
    }

    /**
     * Create an instance of {@link Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo createLevel0DatastripDataStripMetadataFileQualityIndicatorsInfo() {
        return new Level0Datastrip.DataStripMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo }
     * 
     */
    public Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo createLevel0DatastripDataStripMetadataFileAuxiliaryDataInfo() {
        return new Level0Datastrip.DataStripMetadataFile.AuxiliaryDataInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level0Datastrip }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-0_Datastrip_Structure.xsd", name = "Level-0_Datastrip")
    public JAXBElement<Level0Datastrip> createLevel0Datastrip(Level0Datastrip value) {
        return new JAXBElement<Level0Datastrip>(_Level0Datastrip_QNAME, Level0Datastrip.class, null, value);
    }

}

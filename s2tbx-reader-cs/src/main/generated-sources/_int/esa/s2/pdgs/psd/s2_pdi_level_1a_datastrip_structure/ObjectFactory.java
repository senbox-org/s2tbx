//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1a_datastrip_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_1a_datastrip_structure package. 
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

    private final static QName _Level1ADataStrip_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1A_Datastrip_Structure.xsd", "Level-1A_DataStrip");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_1a_datastrip_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1ADataStrip }
     * 
     */
    public Level1ADataStrip createLevel1ADataStrip() {
        return new Level1ADataStrip();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.DataStripMetadataFile }
     * 
     */
    public Level1ADataStrip.DataStripMetadataFile createLevel1ADataStripDataStripMetadataFile() {
        return new Level1ADataStrip.DataStripMetadataFile();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.InventoryMetadata }
     * 
     */
    public Level1ADataStrip.InventoryMetadata createLevel1ADataStripInventoryMetadata() {
        return new Level1ADataStrip.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.QIDATA }
     * 
     */
    public Level1ADataStrip.QIDATA createLevel1ADataStripQIDATA() {
        return new Level1ADataStrip.QIDATA();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.ManifestSafe }
     * 
     */
    public Level1ADataStrip.ManifestSafe createLevel1ADataStripManifestSafe() {
        return new Level1ADataStrip.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.RepInfo }
     * 
     */
    public Level1ADataStrip.RepInfo createLevel1ADataStripRepInfo() {
        return new Level1ADataStrip.RepInfo();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.DataStripMetadataFile.GeneralInfo }
     * 
     */
    public Level1ADataStrip.DataStripMetadataFile.GeneralInfo createLevel1ADataStripDataStripMetadataFileGeneralInfo() {
        return new Level1ADataStrip.DataStripMetadataFile.GeneralInfo();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.DataStripMetadataFile.ImageDataInfo }
     * 
     */
    public Level1ADataStrip.DataStripMetadataFile.ImageDataInfo createLevel1ADataStripDataStripMetadataFileImageDataInfo() {
        return new Level1ADataStrip.DataStripMetadataFile.ImageDataInfo();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo createLevel1ADataStripDataStripMetadataFileQualityIndicatorsInfo() {
        return new Level1ADataStrip.DataStripMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo }
     * 
     */
    public Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo createLevel1ADataStripDataStripMetadataFileAuxiliaryDataInfo() {
        return new Level1ADataStrip.DataStripMetadataFile.AuxiliaryDataInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1ADataStrip }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1A_Datastrip_Structure.xsd", name = "Level-1A_DataStrip")
    public JAXBElement<Level1ADataStrip> createLevel1ADataStrip(Level1ADataStrip value) {
        return new JAXBElement<Level1ADataStrip>(_Level1ADataStrip_QNAME, Level1ADataStrip.class, null, value);
    }

}

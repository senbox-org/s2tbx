//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_structure package. 
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

    private final static QName _Level1CDatastrip_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1C_Datastrip_Structure.xsd", "Level-1C_Datastrip");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1CDatastrip }
     * 
     */
    public Level1CDatastrip createLevel1CDatastrip() {
        return new Level1CDatastrip();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.DataStripMetadataFile }
     * 
     */
    public Level1CDatastrip.DataStripMetadataFile createLevel1CDatastripDataStripMetadataFile() {
        return new Level1CDatastrip.DataStripMetadataFile();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.QIDATA }
     * 
     */
    public Level1CDatastrip.QIDATA createLevel1CDatastripQIDATA() {
        return new Level1CDatastrip.QIDATA();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.InventoryMetadata }
     * 
     */
    public Level1CDatastrip.InventoryMetadata createLevel1CDatastripInventoryMetadata() {
        return new Level1CDatastrip.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.ManifestSafe }
     * 
     */
    public Level1CDatastrip.ManifestSafe createLevel1CDatastripManifestSafe() {
        return new Level1CDatastrip.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.RepInfo }
     * 
     */
    public Level1CDatastrip.RepInfo createLevel1CDatastripRepInfo() {
        return new Level1CDatastrip.RepInfo();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.DataStripMetadataFile.GeneralInfo }
     * 
     */
    public Level1CDatastrip.DataStripMetadataFile.GeneralInfo createLevel1CDatastripDataStripMetadataFileGeneralInfo() {
        return new Level1CDatastrip.DataStripMetadataFile.GeneralInfo();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.DataStripMetadataFile.ImageDataInfo }
     * 
     */
    public Level1CDatastrip.DataStripMetadataFile.ImageDataInfo createLevel1CDatastripDataStripMetadataFileImageDataInfo() {
        return new Level1CDatastrip.DataStripMetadataFile.ImageDataInfo();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.DataStripMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level1CDatastrip.DataStripMetadataFile.QualityIndicatorsInfo createLevel1CDatastripDataStripMetadataFileQualityIndicatorsInfo() {
        return new Level1CDatastrip.DataStripMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link Level1CDatastrip.DataStripMetadataFile.AuxiliaryDataInfo }
     * 
     */
    public Level1CDatastrip.DataStripMetadataFile.AuxiliaryDataInfo createLevel1CDatastripDataStripMetadataFileAuxiliaryDataInfo() {
        return new Level1CDatastrip.DataStripMetadataFile.AuxiliaryDataInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1CDatastrip }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1C_Datastrip_Structure.xsd", name = "Level-1C_Datastrip")
    public JAXBElement<Level1CDatastrip> createLevel1CDatastrip(Level1CDatastrip value) {
        return new JAXBElement<Level1CDatastrip>(_Level1CDatastrip_QNAME, Level1CDatastrip.class, null, value);
    }

}

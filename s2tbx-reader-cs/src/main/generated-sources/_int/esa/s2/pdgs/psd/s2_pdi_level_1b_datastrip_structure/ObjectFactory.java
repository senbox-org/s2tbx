//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.s2_pdi_level_1b_datastrip_structure;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.s2_pdi_level_1b_datastrip_structure package. 
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

    private final static QName _Level1BDatastrip_QNAME = new QName("http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1B_Datastrip_Structure.xsd", "Level-1B_Datastrip");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.s2_pdi_level_1b_datastrip_structure
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1BDatastrip }
     * 
     */
    public Level1BDatastrip createLevel1BDatastrip() {
        return new Level1BDatastrip();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.DataStripMetadataFile }
     * 
     */
    public Level1BDatastrip.DataStripMetadataFile createLevel1BDatastripDataStripMetadataFile() {
        return new Level1BDatastrip.DataStripMetadataFile();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.QIDATA }
     * 
     */
    public Level1BDatastrip.QIDATA createLevel1BDatastripQIDATA() {
        return new Level1BDatastrip.QIDATA();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.InventoryMetadata }
     * 
     */
    public Level1BDatastrip.InventoryMetadata createLevel1BDatastripInventoryMetadata() {
        return new Level1BDatastrip.InventoryMetadata();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.ManifestSafe }
     * 
     */
    public Level1BDatastrip.ManifestSafe createLevel1BDatastripManifestSafe() {
        return new Level1BDatastrip.ManifestSafe();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.RepInfo }
     * 
     */
    public Level1BDatastrip.RepInfo createLevel1BDatastripRepInfo() {
        return new Level1BDatastrip.RepInfo();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.DataStripMetadataFile.GeneralInfo }
     * 
     */
    public Level1BDatastrip.DataStripMetadataFile.GeneralInfo createLevel1BDatastripDataStripMetadataFileGeneralInfo() {
        return new Level1BDatastrip.DataStripMetadataFile.GeneralInfo();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.DataStripMetadataFile.ImageDataInfo }
     * 
     */
    public Level1BDatastrip.DataStripMetadataFile.ImageDataInfo createLevel1BDatastripDataStripMetadataFileImageDataInfo() {
        return new Level1BDatastrip.DataStripMetadataFile.ImageDataInfo();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.DataStripMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level1BDatastrip.DataStripMetadataFile.QualityIndicatorsInfo createLevel1BDatastripDataStripMetadataFileQualityIndicatorsInfo() {
        return new Level1BDatastrip.DataStripMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link Level1BDatastrip.DataStripMetadataFile.AuxiliaryDataInfo }
     * 
     */
    public Level1BDatastrip.DataStripMetadataFile.AuxiliaryDataInfo createLevel1BDatastripDataStripMetadataFileAuxiliaryDataInfo() {
        return new Level1BDatastrip.DataStripMetadataFile.AuxiliaryDataInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1BDatastrip }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/S2_PDI_Level-1B_Datastrip_Structure.xsd", name = "Level-1B_Datastrip")
    public JAXBElement<Level1BDatastrip> createLevel1BDatastrip(Level1BDatastrip value) {
        return new JAXBElement<Level1BDatastrip>(_Level1BDatastrip_QNAME, Level1BDatastrip.class, null, value);
    }

}

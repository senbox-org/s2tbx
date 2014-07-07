//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.inventory_metadata;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.inventory_metadata package. 
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

    private final static QName _InventoryMetadataDetector_QNAME = new QName("http://pdgs.s2.esa.int/PSD/Inventory_Metadata.xsd", "Detector");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.inventory_metadata
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InventoryMetadata }
     * 
     */
    public InventoryMetadata createInventoryMetadata() {
        return new InventoryMetadata();
    }

    /**
     * Create an instance of {@link InventoryMetadata.GeographicLocalization }
     * 
     */
    public InventoryMetadata.GeographicLocalization createInventoryMetadataGeographicLocalization() {
        return new InventoryMetadata.GeographicLocalization();
    }

    /**
     * Create an instance of {@link InventoryMetadata.GeographicLocalization.ListOfGeoPnt }
     * 
     */
    public InventoryMetadata.GeographicLocalization.ListOfGeoPnt createInventoryMetadataGeographicLocalizationListOfGeoPnt() {
        return new InventoryMetadata.GeographicLocalization.ListOfGeoPnt();
    }

    /**
     * Create an instance of {@link InventoryMetadata.GeographicLocalization.ListOfGeoPnt.GeoPnt }
     * 
     */
    public InventoryMetadata.GeographicLocalization.ListOfGeoPnt.GeoPnt createInventoryMetadataGeographicLocalizationListOfGeoPntGeoPnt() {
        return new InventoryMetadata.GeographicLocalization.ListOfGeoPnt.GeoPnt();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/Inventory_Metadata.xsd", name = "Detector", scope = InventoryMetadata.class)
    public JAXBElement<String> createInventoryMetadataDetector(String value) {
        return new JAXBElement<String>(_InventoryMetadataDetector_QNAME, String.class, InventoryMetadata.class, value);
    }

}

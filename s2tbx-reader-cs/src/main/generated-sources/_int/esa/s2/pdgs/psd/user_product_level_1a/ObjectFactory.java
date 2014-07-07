//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_1a;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.user_product_level_1a package. 
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

    private final static QName _Level1AUserProductStructure_QNAME = new QName("http://pdgs.s2.esa.int/PSD/user_product_Level-1A.xsd", "Level-1A_User_Product_Structure");
    private final static QName _Level1AUserProduct_QNAME = new QName("http://pdgs.s2.esa.int/PSD/User_Product_Level-1A.xsd", "Level-1A_User_Product");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.user_product_level_1a
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1AUserProductStructure }
     * 
     */
    public Level1AUserProductStructure createLevel1AUserProductStructure() {
        return new Level1AUserProductStructure();
    }

    /**
     * Create an instance of {@link Level1AUserProduct }
     * 
     */
    public Level1AUserProduct createLevel1AUserProduct() {
        return new Level1AUserProduct();
    }

    /**
     * Create an instance of {@link Level1AUserProductStructure.ProductMetadataFile }
     * 
     */
    public Level1AUserProductStructure.ProductMetadataFile createLevel1AUserProductStructureProductMetadataFile() {
        return new Level1AUserProductStructure.ProductMetadataFile();
    }

    /**
     * Create an instance of {@link Level1AUserProductStructure.GRANULE }
     * 
     */
    public Level1AUserProductStructure.GRANULE createLevel1AUserProductStructureGRANULE() {
        return new Level1AUserProductStructure.GRANULE();
    }

    /**
     * Create an instance of {@link Level1AUserProductStructure.DATASTRIP }
     * 
     */
    public Level1AUserProductStructure.DATASTRIP createLevel1AUserProductStructureDATASTRIP() {
        return new Level1AUserProductStructure.DATASTRIP();
    }

    /**
     * Create an instance of {@link Level1AUserProductStructure.AUXDATA }
     * 
     */
    public Level1AUserProductStructure.AUXDATA createLevel1AUserProductStructureAUXDATA() {
        return new Level1AUserProductStructure.AUXDATA();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1AUserProductStructure }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/user_product_Level-1A.xsd", name = "Level-1A_User_Product_Structure")
    public JAXBElement<Level1AUserProductStructure> createLevel1AUserProductStructure(Level1AUserProductStructure value) {
        return new JAXBElement<Level1AUserProductStructure>(_Level1AUserProductStructure_QNAME, Level1AUserProductStructure.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1AUserProduct }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/User_Product_Level-1A.xsd", name = "Level-1A_User_Product")
    public JAXBElement<Level1AUserProduct> createLevel1AUserProduct(Level1AUserProduct value) {
        return new JAXBElement<Level1AUserProduct>(_Level1AUserProduct_QNAME, Level1AUserProduct.class, null, value);
    }

}

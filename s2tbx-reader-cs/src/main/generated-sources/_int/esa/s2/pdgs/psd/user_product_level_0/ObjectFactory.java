//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_0;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.user_product_level_0 package. 
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

    private final static QName _Level0UserProduct_QNAME = new QName("http://pdgs.s2.esa.int/PSD/User_Product_Level-0.xsd", "Level-0_User_Product");
    private final static QName _Level0UserProductStructure_QNAME = new QName("http://pdgs.s2.esa.int/PSD/user_product_Level-0.xsd", "Level-0_User_Product_Structure");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.user_product_level_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level0UserProductStructure }
     * 
     */
    public Level0UserProductStructure createLevel0UserProductStructure() {
        return new Level0UserProductStructure();
    }

    /**
     * Create an instance of {@link Level0UserProduct }
     * 
     */
    public Level0UserProduct createLevel0UserProduct() {
        return new Level0UserProduct();
    }

    /**
     * Create an instance of {@link Level0UserProductStructure.ProductMetadataFile }
     * 
     */
    public Level0UserProductStructure.ProductMetadataFile createLevel0UserProductStructureProductMetadataFile() {
        return new Level0UserProductStructure.ProductMetadataFile();
    }

    /**
     * Create an instance of {@link Level0UserProductStructure.GRANULE }
     * 
     */
    public Level0UserProductStructure.GRANULE createLevel0UserProductStructureGRANULE() {
        return new Level0UserProductStructure.GRANULE();
    }

    /**
     * Create an instance of {@link Level0UserProductStructure.DATASTRIP }
     * 
     */
    public Level0UserProductStructure.DATASTRIP createLevel0UserProductStructureDATASTRIP() {
        return new Level0UserProductStructure.DATASTRIP();
    }

    /**
     * Create an instance of {@link Level0UserProductStructure.AUXDATA }
     * 
     */
    public Level0UserProductStructure.AUXDATA createLevel0UserProductStructureAUXDATA() {
        return new Level0UserProductStructure.AUXDATA();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level0UserProduct }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/User_Product_Level-0.xsd", name = "Level-0_User_Product")
    public JAXBElement<Level0UserProduct> createLevel0UserProduct(Level0UserProduct value) {
        return new JAXBElement<Level0UserProduct>(_Level0UserProduct_QNAME, Level0UserProduct.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level0UserProductStructure }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/user_product_Level-0.xsd", name = "Level-0_User_Product_Structure")
    public JAXBElement<Level0UserProductStructure> createLevel0UserProductStructure(Level0UserProductStructure value) {
        return new JAXBElement<Level0UserProductStructure>(_Level0UserProductStructure_QNAME, Level0UserProductStructure.class, null, value);
    }

}

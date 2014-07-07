//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_1c;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.user_product_level_1c package. 
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

    private final static QName _Level1CUserProductStructure_QNAME = new QName("http://pdgs.s2.esa.int/PSD/user_product_Level-1C.xsd", "Level-1C_User_Product_Structure");
    private final static QName _Level1CUserProduct_QNAME = new QName("http://pdgs.s2.esa.int/PSD/User_Product_Level-1C.xsd", "Level-1C_User_Product");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.user_product_level_1c
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1CUserProductStructure }
     * 
     */
    public Level1CUserProductStructure createLevel1CUserProductStructure() {
        return new Level1CUserProductStructure();
    }

    /**
     * Create an instance of {@link Level1CUserProductStructure.GRANULE }
     * 
     */
    public Level1CUserProductStructure.GRANULE createLevel1CUserProductStructureGRANULE() {
        return new Level1CUserProductStructure.GRANULE();
    }

    /**
     * Create an instance of {@link Level1CUserProductStructure.GRANULE.Tiles }
     * 
     */
    public Level1CUserProductStructure.GRANULE.Tiles createLevel1CUserProductStructureGRANULETiles() {
        return new Level1CUserProductStructure.GRANULE.Tiles();
    }

    /**
     * Create an instance of {@link Level1CUserProduct }
     * 
     */
    public Level1CUserProduct createLevel1CUserProduct() {
        return new Level1CUserProduct();
    }

    /**
     * Create an instance of {@link Level1CUserProductStructure.ProductMetadataFile }
     * 
     */
    public Level1CUserProductStructure.ProductMetadataFile createLevel1CUserProductStructureProductMetadataFile() {
        return new Level1CUserProductStructure.ProductMetadataFile();
    }

    /**
     * Create an instance of {@link Level1CUserProductStructure.DATASTRIP }
     * 
     */
    public Level1CUserProductStructure.DATASTRIP createLevel1CUserProductStructureDATASTRIP() {
        return new Level1CUserProductStructure.DATASTRIP();
    }

    /**
     * Create an instance of {@link Level1CUserProductStructure.AUXDATA }
     * 
     */
    public Level1CUserProductStructure.AUXDATA createLevel1CUserProductStructureAUXDATA() {
        return new Level1CUserProductStructure.AUXDATA();
    }

    /**
     * Create an instance of {@link Level1CUserProductStructure.GRANULE.Tiles.AUXDATA }
     * 
     */
    public Level1CUserProductStructure.GRANULE.Tiles.AUXDATA createLevel1CUserProductStructureGRANULETilesAUXDATA() {
        return new Level1CUserProductStructure.GRANULE.Tiles.AUXDATA();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1CUserProductStructure }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/user_product_Level-1C.xsd", name = "Level-1C_User_Product_Structure")
    public JAXBElement<Level1CUserProductStructure> createLevel1CUserProductStructure(Level1CUserProductStructure value) {
        return new JAXBElement<Level1CUserProductStructure>(_Level1CUserProductStructure_QNAME, Level1CUserProductStructure.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1CUserProduct }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/User_Product_Level-1C.xsd", name = "Level-1C_User_Product")
    public JAXBElement<Level1CUserProduct> createLevel1CUserProduct(Level1CUserProduct value) {
        return new JAXBElement<Level1CUserProduct>(_Level1CUserProduct_QNAME, Level1CUserProduct.class, null, value);
    }

}

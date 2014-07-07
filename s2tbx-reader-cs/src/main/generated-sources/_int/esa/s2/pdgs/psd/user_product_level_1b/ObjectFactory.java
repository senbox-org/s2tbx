//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.s2.pdgs.psd.user_product_level_1b;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the _int.esa.s2.pdgs.psd.user_product_level_1b package. 
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

    private final static QName _Level1BUserProduct_QNAME = new QName("http://pdgs.s2.esa.int/PSD/User_Product_Level-1B.xsd", "Level-1B_User_Product");
    private final static QName _Level1BUserProductStructure_QNAME = new QName("http://pdgs.s2.esa.int/PSD/user_product_Level-1B.xsd", "Level-1B_User_Product_Structure");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: _int.esa.s2.pdgs.psd.user_product_level_1b
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure }
     * 
     */
    public Level1BUserProductStructure createLevel1BUserProductStructure() {
        return new Level1BUserProductStructure();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.ProductMetadataFile }
     * 
     */
    public Level1BUserProductStructure.ProductMetadataFile createLevel1BUserProductStructureProductMetadataFile() {
        return new Level1BUserProductStructure.ProductMetadataFile();
    }

    /**
     * Create an instance of {@link Level1BUserProduct }
     * 
     */
    public Level1BUserProduct createLevel1BUserProduct() {
        return new Level1BUserProduct();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.GRANULE }
     * 
     */
    public Level1BUserProductStructure.GRANULE createLevel1BUserProductStructureGRANULE() {
        return new Level1BUserProductStructure.GRANULE();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.DATASTRIP }
     * 
     */
    public Level1BUserProductStructure.DATASTRIP createLevel1BUserProductStructureDATASTRIP() {
        return new Level1BUserProductStructure.DATASTRIP();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.AUXDATA }
     * 
     */
    public Level1BUserProductStructure.AUXDATA createLevel1BUserProductStructureAUXDATA() {
        return new Level1BUserProductStructure.AUXDATA();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.ProductMetadataFile.GeneralInfo }
     * 
     */
    public Level1BUserProductStructure.ProductMetadataFile.GeneralInfo createLevel1BUserProductStructureProductMetadataFileGeneralInfo() {
        return new Level1BUserProductStructure.ProductMetadataFile.GeneralInfo();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.ProductMetadataFile.GeometricInfo }
     * 
     */
    public Level1BUserProductStructure.ProductMetadataFile.GeometricInfo createLevel1BUserProductStructureProductMetadataFileGeometricInfo() {
        return new Level1BUserProductStructure.ProductMetadataFile.GeometricInfo();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo }
     * 
     */
    public Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo createLevel1BUserProductStructureProductMetadataFileAuxiliaryDataInfo() {
        return new Level1BUserProductStructure.ProductMetadataFile.AuxiliaryDataInfo();
    }

    /**
     * Create an instance of {@link Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo }
     * 
     */
    public Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo createLevel1BUserProductStructureProductMetadataFileQualityIndicatorsInfo() {
        return new Level1BUserProductStructure.ProductMetadataFile.QualityIndicatorsInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1BUserProduct }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/User_Product_Level-1B.xsd", name = "Level-1B_User_Product")
    public JAXBElement<Level1BUserProduct> createLevel1BUserProduct(Level1BUserProduct value) {
        return new JAXBElement<Level1BUserProduct>(_Level1BUserProduct_QNAME, Level1BUserProduct.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Level1BUserProductStructure }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://pdgs.s2.esa.int/PSD/user_product_Level-1B.xsd", name = "Level-1B_User_Product_Structure")
    public JAXBElement<Level1BUserProductStructure> createLevel1BUserProductStructure(Level1BUserProductStructure value) {
        return new JAXBElement<Level1BUserProductStructure>(_Level1BUserProductStructure_QNAME, Level1BUserProductStructure.class, null, value);
    }

}

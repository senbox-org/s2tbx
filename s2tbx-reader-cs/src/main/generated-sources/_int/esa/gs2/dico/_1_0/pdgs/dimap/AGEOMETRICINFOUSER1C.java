//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_GEOMETRIC_INFO_USER1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GEOMETRIC_INFO_USER1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Product_Footprint" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_INIT_LOC_PROD_PRODUCT_FOOTPRINT"/>
 *         &lt;element name="Coordinate_Reference_System" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_COORDINATE_REFERENCE_SYSTEM_L1C"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GEOMETRIC_INFO_USER1C", propOrder = {
    "productFootprint",
    "coordinateReferenceSystem"
})
public class AGEOMETRICINFOUSER1C {

    @XmlElement(name = "Product_Footprint", required = true)
    protected ANINITLOCPRODPRODUCTFOOTPRINT productFootprint;
    @XmlElement(name = "Coordinate_Reference_System", required = true)
    protected ACOORDINATEREFERENCESYSTEML1C coordinateReferenceSystem;

    /**
     * Obtient la valeur de la propriété productFootprint.
     * 
     * @return
     *     possible object is
     *     {@link ANINITLOCPRODPRODUCTFOOTPRINT }
     *     
     */
    public ANINITLOCPRODPRODUCTFOOTPRINT getProductFootprint() {
        return productFootprint;
    }

    /**
     * Définit la valeur de la propriété productFootprint.
     * 
     * @param value
     *     allowed object is
     *     {@link ANINITLOCPRODPRODUCTFOOTPRINT }
     *     
     */
    public void setProductFootprint(ANINITLOCPRODPRODUCTFOOTPRINT value) {
        this.productFootprint = value;
    }

    /**
     * Obtient la valeur de la propriété coordinateReferenceSystem.
     * 
     * @return
     *     possible object is
     *     {@link ACOORDINATEREFERENCESYSTEML1C }
     *     
     */
    public ACOORDINATEREFERENCESYSTEML1C getCoordinateReferenceSystem() {
        return coordinateReferenceSystem;
    }

    /**
     * Définit la valeur de la propriété coordinateReferenceSystem.
     * 
     * @param value
     *     allowed object is
     *     {@link ACOORDINATEREFERENCESYSTEML1C }
     *     
     */
    public void setCoordinateReferenceSystem(ACOORDINATEREFERENCESYSTEML1C value) {
        this.coordinateReferenceSystem = value;
    }

}

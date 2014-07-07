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
 * <p>Classe Java pour A_GEOMETRIC_INFO_USERL0L1AL1B complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GEOMETRIC_INFO_USERL0L1AL1B">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Product_Footprint" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_INIT_LOC_PROD_PRODUCT_FOOTPRINT"/>
 *         &lt;element name="Coordinate_Reference_System" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_COORDINATE_REFERENCE_SYSTEM_L0_L1A_L1B"/>
 *         &lt;element name="Geometric_Header_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_HEADER_LIST"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GEOMETRIC_INFO_USERL0L1AL1B", propOrder = {
    "productFootprint",
    "coordinateReferenceSystem",
    "geometricHeaderList"
})
public class AGEOMETRICINFOUSERL0L1AL1B {

    @XmlElement(name = "Product_Footprint", required = true)
    protected ANINITLOCPRODPRODUCTFOOTPRINT productFootprint;
    @XmlElement(name = "Coordinate_Reference_System", required = true)
    protected ACOORDINATEREFERENCESYSTEML0L1AL1B coordinateReferenceSystem;
    @XmlElement(name = "Geometric_Header_List", required = true)
    protected AGEOMETRICHEADERLIST geometricHeaderList;

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
     *     {@link ACOORDINATEREFERENCESYSTEML0L1AL1B }
     *     
     */
    public ACOORDINATEREFERENCESYSTEML0L1AL1B getCoordinateReferenceSystem() {
        return coordinateReferenceSystem;
    }

    /**
     * Définit la valeur de la propriété coordinateReferenceSystem.
     * 
     * @param value
     *     allowed object is
     *     {@link ACOORDINATEREFERENCESYSTEML0L1AL1B }
     *     
     */
    public void setCoordinateReferenceSystem(ACOORDINATEREFERENCESYSTEML0L1AL1B value) {
        this.coordinateReferenceSystem = value;
    }

    /**
     * Obtient la valeur de la propriété geometricHeaderList.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICHEADERLIST }
     *     
     */
    public AGEOMETRICHEADERLIST getGeometricHeaderList() {
        return geometricHeaderList;
    }

    /**
     * Définit la valeur de la propriété geometricHeaderList.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICHEADERLIST }
     *     
     */
    public void setGeometricHeaderList(AGEOMETRICHEADERLIST value) {
        this.geometricHeaderList = value;
    }

}

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
 * <p>Classe Java pour A_DATASTRIP_L1B_L1C_GEOM_QI complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATASTRIP_L1B_L1C_GEOM_QI">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Geometric_QI" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_COMMON_GEOM_QI"/>
 *         &lt;element name="Geometric_Refining_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_REFINING_QUALITY_L1B_L1C"/>
 *         &lt;element name="Update_Absolute_Location" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ABSOLUTE_LOCATION"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATASTRIP_L1B_L1C_GEOM_QI", propOrder = {
    "geometricQI",
    "geometricRefiningQuality",
    "updateAbsoluteLocation"
})
public class ADATASTRIPL1BL1CGEOMQI {

    @XmlElement(name = "Geometric_QI", required = true)
    protected ADATASTRIPCOMMONGEOMQI geometricQI;
    @XmlElement(name = "Geometric_Refining_Quality", required = true)
    protected AGEOMETRICREFININGQUALITYL1BL1C geometricRefiningQuality;
    @XmlElement(name = "Update_Absolute_Location", required = true)
    protected ANABSOLUTELOCATION updateAbsoluteLocation;

    /**
     * Obtient la valeur de la propriété geometricQI.
     * 
     * @return
     *     possible object is
     *     {@link ADATASTRIPCOMMONGEOMQI }
     *     
     */
    public ADATASTRIPCOMMONGEOMQI getGeometricQI() {
        return geometricQI;
    }

    /**
     * Définit la valeur de la propriété geometricQI.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATASTRIPCOMMONGEOMQI }
     *     
     */
    public void setGeometricQI(ADATASTRIPCOMMONGEOMQI value) {
        this.geometricQI = value;
    }

    /**
     * Obtient la valeur de la propriété geometricRefiningQuality.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICREFININGQUALITYL1BL1C }
     *     
     */
    public AGEOMETRICREFININGQUALITYL1BL1C getGeometricRefiningQuality() {
        return geometricRefiningQuality;
    }

    /**
     * Définit la valeur de la propriété geometricRefiningQuality.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICREFININGQUALITYL1BL1C }
     *     
     */
    public void setGeometricRefiningQuality(AGEOMETRICREFININGQUALITYL1BL1C value) {
        this.geometricRefiningQuality = value;
    }

    /**
     * Obtient la valeur de la propriété updateAbsoluteLocation.
     * 
     * @return
     *     possible object is
     *     {@link ANABSOLUTELOCATION }
     *     
     */
    public ANABSOLUTELOCATION getUpdateAbsoluteLocation() {
        return updateAbsoluteLocation;
    }

    /**
     * Définit la valeur de la propriété updateAbsoluteLocation.
     * 
     * @param value
     *     allowed object is
     *     {@link ANABSOLUTELOCATION }
     *     
     */
    public void setUpdateAbsoluteLocation(ANABSOLUTELOCATION value) {
        this.updateAbsoluteLocation = value;
    }

}

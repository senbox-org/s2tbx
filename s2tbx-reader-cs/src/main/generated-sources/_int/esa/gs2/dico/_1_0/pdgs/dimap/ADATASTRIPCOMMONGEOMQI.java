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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_DATASTRIP_COMMON_GEOM_QI complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATASTRIP_COMMON_GEOM_QI">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Absolute_Location" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ABSOLUTE_LOCATION"/>
 *         &lt;element name="Planimetric_Stability" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PLANIMETRIC_STABILITY"/>
 *         &lt;element name="EPHEMERIS_QUALITY" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_QUALITY"/>
 *         &lt;element name="ANCILLARY_QUALITY" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ANCILLARY_QUALITY"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATASTRIP_COMMON_GEOM_QI", propOrder = {
    "absoluteLocation",
    "planimetricStability",
    "ephemerisquality",
    "ancillaryquality"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AQUALITYINDICATORSINFODSL0 .GeometricInfo.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.AQUALITYINDICATORSINFODSL1A.GeometricInfo.class
})
public class ADATASTRIPCOMMONGEOMQI {

    @XmlElement(name = "Absolute_Location", required = true)
    protected ANABSOLUTELOCATION absoluteLocation;
    @XmlElement(name = "Planimetric_Stability", required = true)
    protected APLANIMETRICSTABILITY planimetricStability;
    @XmlElement(name = "EPHEMERIS_QUALITY")
    protected double ephemerisquality;
    @XmlElement(name = "ANCILLARY_QUALITY")
    protected double ancillaryquality;

    /**
     * Obtient la valeur de la propriété absoluteLocation.
     * 
     * @return
     *     possible object is
     *     {@link ANABSOLUTELOCATION }
     *     
     */
    public ANABSOLUTELOCATION getAbsoluteLocation() {
        return absoluteLocation;
    }

    /**
     * Définit la valeur de la propriété absoluteLocation.
     * 
     * @param value
     *     allowed object is
     *     {@link ANABSOLUTELOCATION }
     *     
     */
    public void setAbsoluteLocation(ANABSOLUTELOCATION value) {
        this.absoluteLocation = value;
    }

    /**
     * Obtient la valeur de la propriété planimetricStability.
     * 
     * @return
     *     possible object is
     *     {@link APLANIMETRICSTABILITY }
     *     
     */
    public APLANIMETRICSTABILITY getPlanimetricStability() {
        return planimetricStability;
    }

    /**
     * Définit la valeur de la propriété planimetricStability.
     * 
     * @param value
     *     allowed object is
     *     {@link APLANIMETRICSTABILITY }
     *     
     */
    public void setPlanimetricStability(APLANIMETRICSTABILITY value) {
        this.planimetricStability = value;
    }

    /**
     * Obtient la valeur de la propriété ephemerisquality.
     * 
     */
    public double getEPHEMERISQUALITY() {
        return ephemerisquality;
    }

    /**
     * Définit la valeur de la propriété ephemerisquality.
     * 
     */
    public void setEPHEMERISQUALITY(double value) {
        this.ephemerisquality = value;
    }

    /**
     * Obtient la valeur de la propriété ancillaryquality.
     * 
     */
    public double getANCILLARYQUALITY() {
        return ancillaryquality;
    }

    /**
     * Définit la valeur de la propriété ancillaryquality.
     * 
     */
    public void setANCILLARYQUALITY(double value) {
        this.ancillaryquality = value;
    }

}

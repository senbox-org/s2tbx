//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour AN_INCIDENCE_ANGLE_GRID complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_INCIDENCE_ANGLE_GRID">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Zenith" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ANGLE_GRID"/>
 *         &lt;element name="Azimuth" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ANGLE_GRID"/>
 *       &lt;/sequence>
 *       &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *       &lt;attribute name="detectorId" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_INCIDENCE_ANGLE_GRID", propOrder = {
    "zenith",
    "azimuth"
})
public class ANINCIDENCEANGLEGRID {

    @XmlElement(name = "Zenith", required = true)
    protected ANANGLEGRID zenith;
    @XmlElement(name = "Azimuth", required = true)
    protected ANANGLEGRID azimuth;
    @XmlAttribute(name = "bandId", required = true)
    protected String bandId;
    @XmlAttribute(name = "detectorId", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String detectorId;

    /**
     * Obtient la valeur de la propriété zenith.
     * 
     * @return
     *     possible object is
     *     {@link ANANGLEGRID }
     *     
     */
    public ANANGLEGRID getZenith() {
        return zenith;
    }

    /**
     * Définit la valeur de la propriété zenith.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANGLEGRID }
     *     
     */
    public void setZenith(ANANGLEGRID value) {
        this.zenith = value;
    }

    /**
     * Obtient la valeur de la propriété azimuth.
     * 
     * @return
     *     possible object is
     *     {@link ANANGLEGRID }
     *     
     */
    public ANANGLEGRID getAzimuth() {
        return azimuth;
    }

    /**
     * Définit la valeur de la propriété azimuth.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANGLEGRID }
     *     
     */
    public void setAzimuth(ANANGLEGRID value) {
        this.azimuth = value;
    }

    /**
     * Obtient la valeur de la propriété bandId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBandId() {
        return bandId;
    }

    /**
     * Définit la valeur de la propriété bandId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBandId(String value) {
        this.bandId = value;
    }

    /**
     * Obtient la valeur de la propriété detectorId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetectorId() {
        return detectorId;
    }

    /**
     * Définit la valeur de la propriété detectorId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetectorId(String value) {
        this.detectorId = value;
    }

}

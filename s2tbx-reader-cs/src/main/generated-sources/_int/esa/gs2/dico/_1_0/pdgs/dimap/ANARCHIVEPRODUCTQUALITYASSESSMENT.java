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
 * <p>Classe Java pour AN_ARCHIVE_PRODUCT_QUALITY_ASSESSMENT complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ARCHIVE_PRODUCT_QUALITY_ASSESSMENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Absolute_Location" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ABSOLUTE_LOCATION" minOccurs="0"/>
 *         &lt;element name="Planimetric_Stability" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PLANIMETRIC_STABILITY" minOccurs="0"/>
 *         &lt;element name="EPHEMERIS_QUALITY" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_QUALITY" minOccurs="0"/>
 *         &lt;element name="Mask_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_MASK_LIST" minOccurs="0"/>
 *         &lt;element name="CLOUDY_PIXEL_PERCENTAGE" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_CLOUDY_PIXEL_PERCENTAGE" minOccurs="0"/>
 *         &lt;element name="Data_Content_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATA_CONTENT_QUALITY" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_ARCHIVE_PRODUCT_QUALITY_ASSESSMENT", propOrder = {
    "absoluteLocation",
    "planimetricStability",
    "ephemerisquality",
    "maskList",
    "cloudypixelpercentage",
    "dataContentQuality"
})
public class ANARCHIVEPRODUCTQUALITYASSESSMENT {

    @XmlElement(name = "Absolute_Location")
    protected ANABSOLUTELOCATION absoluteLocation;
    @XmlElement(name = "Planimetric_Stability")
    protected APLANIMETRICSTABILITY planimetricStability;
    @XmlElement(name = "EPHEMERIS_QUALITY")
    protected Double ephemerisquality;
    @XmlElement(name = "Mask_List")
    protected AMASKLIST maskList;
    @XmlElement(name = "CLOUDY_PIXEL_PERCENTAGE")
    protected Double cloudypixelpercentage;
    @XmlElement(name = "Data_Content_Quality")
    protected ADATACONTENTQUALITY dataContentQuality;

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
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getEPHEMERISQUALITY() {
        return ephemerisquality;
    }

    /**
     * Définit la valeur de la propriété ephemerisquality.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setEPHEMERISQUALITY(Double value) {
        this.ephemerisquality = value;
    }

    /**
     * Obtient la valeur de la propriété maskList.
     * 
     * @return
     *     possible object is
     *     {@link AMASKLIST }
     *     
     */
    public AMASKLIST getMaskList() {
        return maskList;
    }

    /**
     * Définit la valeur de la propriété maskList.
     * 
     * @param value
     *     allowed object is
     *     {@link AMASKLIST }
     *     
     */
    public void setMaskList(AMASKLIST value) {
        this.maskList = value;
    }

    /**
     * Obtient la valeur de la propriété cloudypixelpercentage.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCLOUDYPIXELPERCENTAGE() {
        return cloudypixelpercentage;
    }

    /**
     * Définit la valeur de la propriété cloudypixelpercentage.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCLOUDYPIXELPERCENTAGE(Double value) {
        this.cloudypixelpercentage = value;
    }

    /**
     * Obtient la valeur de la propriété dataContentQuality.
     * 
     * @return
     *     possible object is
     *     {@link ADATACONTENTQUALITY }
     *     
     */
    public ADATACONTENTQUALITY getDataContentQuality() {
        return dataContentQuality;
    }

    /**
     * Définit la valeur de la propriété dataContentQuality.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATACONTENTQUALITY }
     *     
     */
    public void setDataContentQuality(ADATACONTENTQUALITY value) {
        this.dataContentQuality = value;
    }

}

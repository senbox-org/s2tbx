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
 * Synthesis of the OLQC checks
 * 
 * <p>Classe Java pour A_QUALITY_SUMMARY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUALITY_SUMMARY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CLOUDY_PIXEL_PERCENTAGE_SUMMARY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DEGRADED_ANC_DATA_PERCENTAGE_SUMMARY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DEGRADED_MSI_DATA_PERCENTAGE_SUMMARY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="EPHEMERIS_QUALITY_SUMMARY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ANCILLARY_QUALITY_SUMMARY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_QUALITY_SUMMARY", propOrder = {
    "cloudypixelpercentagesummaryflag",
    "degradedancdatapercentagesummaryflag",
    "degradedmsidatapercentagesummaryflag",
    "ephemerisqualitysummaryflag",
    "ancillaryqualitysummaryflag"
})
public class AQUALITYSUMMARY {

    @XmlElement(name = "CLOUDY_PIXEL_PERCENTAGE_SUMMARY_FLAG", required = true)
    protected String cloudypixelpercentagesummaryflag;
    @XmlElement(name = "DEGRADED_ANC_DATA_PERCENTAGE_SUMMARY_FLAG", required = true)
    protected String degradedancdatapercentagesummaryflag;
    @XmlElement(name = "DEGRADED_MSI_DATA_PERCENTAGE_SUMMARY_FLAG", required = true)
    protected String degradedmsidatapercentagesummaryflag;
    @XmlElement(name = "EPHEMERIS_QUALITY_SUMMARY_FLAG", required = true)
    protected String ephemerisqualitysummaryflag;
    @XmlElement(name = "ANCILLARY_QUALITY_SUMMARY_FLAG", required = true)
    protected String ancillaryqualitysummaryflag;

    /**
     * Obtient la valeur de la propriété cloudypixelpercentagesummaryflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCLOUDYPIXELPERCENTAGESUMMARYFLAG() {
        return cloudypixelpercentagesummaryflag;
    }

    /**
     * Définit la valeur de la propriété cloudypixelpercentagesummaryflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCLOUDYPIXELPERCENTAGESUMMARYFLAG(String value) {
        this.cloudypixelpercentagesummaryflag = value;
    }

    /**
     * Obtient la valeur de la propriété degradedancdatapercentagesummaryflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEGRADEDANCDATAPERCENTAGESUMMARYFLAG() {
        return degradedancdatapercentagesummaryflag;
    }

    /**
     * Définit la valeur de la propriété degradedancdatapercentagesummaryflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEGRADEDANCDATAPERCENTAGESUMMARYFLAG(String value) {
        this.degradedancdatapercentagesummaryflag = value;
    }

    /**
     * Obtient la valeur de la propriété degradedmsidatapercentagesummaryflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEGRADEDMSIDATAPERCENTAGESUMMARYFLAG() {
        return degradedmsidatapercentagesummaryflag;
    }

    /**
     * Définit la valeur de la propriété degradedmsidatapercentagesummaryflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEGRADEDMSIDATAPERCENTAGESUMMARYFLAG(String value) {
        this.degradedmsidatapercentagesummaryflag = value;
    }

    /**
     * Obtient la valeur de la propriété ephemerisqualitysummaryflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEPHEMERISQUALITYSUMMARYFLAG() {
        return ephemerisqualitysummaryflag;
    }

    /**
     * Définit la valeur de la propriété ephemerisqualitysummaryflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEPHEMERISQUALITYSUMMARYFLAG(String value) {
        this.ephemerisqualitysummaryflag = value;
    }

    /**
     * Obtient la valeur de la propriété ancillaryqualitysummaryflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getANCILLARYQUALITYSUMMARYFLAG() {
        return ancillaryqualitysummaryflag;
    }

    /**
     * Définit la valeur de la propriété ancillaryqualitysummaryflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setANCILLARYQUALITYSUMMARYFLAG(String value) {
        this.ancillaryqualitysummaryflag = value;
    }

}

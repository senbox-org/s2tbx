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
 * <p>Classe Java pour A_QUALITY_SUMMARY_L1B_L1C_USER complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUALITY_SUMMARY_L1B_L1C_USER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SENSOR_QUALITY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="GEOMETRIC_QUALITY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="GENERAL_QUALITY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FORMAT_CORRECTNESS_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PASSED"/>
 *               &lt;enumeration value="FAILED"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="RADIOMETRIC_QUALITY_FLAG">
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
@XmlType(name = "A_QUALITY_SUMMARY_L1B_L1C_USER", propOrder = {
    "sensorqualityflag",
    "geometricqualityflag",
    "generalqualityflag",
    "formatcorrectnessflag",
    "radiometricqualityflag"
})
public class AQUALITYSUMMARYL1BL1CUSER {

    @XmlElement(name = "SENSOR_QUALITY_FLAG", required = true)
    protected String sensorqualityflag;
    @XmlElement(name = "GEOMETRIC_QUALITY_FLAG", required = true)
    protected String geometricqualityflag;
    @XmlElement(name = "GENERAL_QUALITY_FLAG", required = true)
    protected String generalqualityflag;
    @XmlElement(name = "FORMAT_CORRECTNESS_FLAG", required = true)
    protected String formatcorrectnessflag;
    @XmlElement(name = "RADIOMETRIC_QUALITY_FLAG", required = true)
    protected String radiometricqualityflag;

    /**
     * Obtient la valeur de la propriété sensorqualityflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSENSORQUALITYFLAG() {
        return sensorqualityflag;
    }

    /**
     * Définit la valeur de la propriété sensorqualityflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSENSORQUALITYFLAG(String value) {
        this.sensorqualityflag = value;
    }

    /**
     * Obtient la valeur de la propriété geometricqualityflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGEOMETRICQUALITYFLAG() {
        return geometricqualityflag;
    }

    /**
     * Définit la valeur de la propriété geometricqualityflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGEOMETRICQUALITYFLAG(String value) {
        this.geometricqualityflag = value;
    }

    /**
     * Obtient la valeur de la propriété generalqualityflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGENERALQUALITYFLAG() {
        return generalqualityflag;
    }

    /**
     * Définit la valeur de la propriété generalqualityflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGENERALQUALITYFLAG(String value) {
        this.generalqualityflag = value;
    }

    /**
     * Obtient la valeur de la propriété formatcorrectnessflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFORMATCORRECTNESSFLAG() {
        return formatcorrectnessflag;
    }

    /**
     * Définit la valeur de la propriété formatcorrectnessflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFORMATCORRECTNESSFLAG(String value) {
        this.formatcorrectnessflag = value;
    }

    /**
     * Obtient la valeur de la propriété radiometricqualityflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRADIOMETRICQUALITYFLAG() {
        return radiometricqualityflag;
    }

    /**
     * Définit la valeur de la propriété radiometricqualityflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRADIOMETRICQUALITYFLAG(String value) {
        this.radiometricqualityflag = value;
    }

}

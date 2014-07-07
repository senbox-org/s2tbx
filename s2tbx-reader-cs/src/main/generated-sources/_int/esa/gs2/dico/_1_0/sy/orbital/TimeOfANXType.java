//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Time_of_ANX_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Time_of_ANX_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TAI" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}TAI_Date_Time_Type"/>
 *         &lt;element name="UTC" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}UTC_Date_Time_Type"/>
 *         &lt;element name="UT1" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}UT1_Date_Time_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time_of_ANX_Type", propOrder = {
    "tai",
    "utc",
    "ut1"
})
public class TimeOfANXType {

    @XmlElement(name = "TAI", required = true)
    protected String tai;
    @XmlElement(name = "UTC", required = true)
    protected String utc;
    @XmlElement(name = "UT1", required = true)
    protected String ut1;

    /**
     * Obtient la valeur de la propriété tai.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTAI() {
        return tai;
    }

    /**
     * Définit la valeur de la propriété tai.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTAI(String value) {
        this.tai = value;
    }

    /**
     * Obtient la valeur de la propriété utc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUTC() {
        return utc;
    }

    /**
     * Définit la valeur de la propriété utc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUTC(String value) {
        this.utc = value;
    }

    /**
     * Obtient la valeur de la propriété ut1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUT1() {
        return ut1;
    }

    /**
     * Définit la valeur de la propriété ut1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUT1(String value) {
        this.ut1 = value;
    }

}

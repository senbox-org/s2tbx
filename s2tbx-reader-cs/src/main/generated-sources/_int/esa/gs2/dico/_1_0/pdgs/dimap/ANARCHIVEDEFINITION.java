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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Initial archiving station
 * 
 * <p>Classe Java pour AN_ARCHIVE_DEFINITION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ARCHIVE_DEFINITION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UTC_ARCHIVING_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_SEC_DATE_TIME"/>
 *         &lt;element name="ARCHIVING_CENTER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_ARCHIVE_DEFINITION", propOrder = {
    "utcarchivingtime",
    "archivingcenter"
})
public class ANARCHIVEDEFINITION {

    @XmlElement(name = "UTC_ARCHIVING_TIME", required = true)
    protected XMLGregorianCalendar utcarchivingtime;
    @XmlElement(name = "ARCHIVING_CENTER", required = true)
    protected String archivingcenter;

    /**
     * Obtient la valeur de la propriété utcarchivingtime.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUTCARCHIVINGTIME() {
        return utcarchivingtime;
    }

    /**
     * Définit la valeur de la propriété utcarchivingtime.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUTCARCHIVINGTIME(XMLGregorianCalendar value) {
        this.utcarchivingtime = value;
    }

    /**
     * Obtient la valeur de la propriété archivingcenter.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getARCHIVINGCENTER() {
        return archivingcenter;
    }

    /**
     * Définit la valeur de la propriété archivingcenter.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setARCHIVINGCENTER(String value) {
        this.archivingcenter = value;
    }

}

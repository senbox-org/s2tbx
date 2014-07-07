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
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour A_DATATAKE_IDENTIFICATION_SAFE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATATAKE_IDENTIFICATION_SAFE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATATAKE_SENSING_STOP" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *       &lt;/sequence>
 *       &lt;attribute name="datatakeIdentifier" use="required" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}DATATAKE_ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATATAKE_IDENTIFICATION_SAFE", propOrder = {
    "datatakesensingstop"
})
public class ADATATAKEIDENTIFICATIONSAFE {

    @XmlElement(name = "DATATAKE_SENSING_STOP", required = true)
    protected XMLGregorianCalendar datatakesensingstop;
    @XmlAttribute(name = "datatakeIdentifier", required = true)
    protected String datatakeIdentifier;

    /**
     * Obtient la valeur de la propriété datatakesensingstop.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATATAKESENSINGSTOP() {
        return datatakesensingstop;
    }

    /**
     * Définit la valeur de la propriété datatakesensingstop.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATATAKESENSINGSTOP(XMLGregorianCalendar value) {
        this.datatakesensingstop = value;
    }

    /**
     * Obtient la valeur de la propriété datatakeIdentifier.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatatakeIdentifier() {
        return datatakeIdentifier;
    }

    /**
     * Définit la valeur de la propriété datatakeIdentifier.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatatakeIdentifier(String value) {
        this.datatakeIdentifier = value;
    }

}

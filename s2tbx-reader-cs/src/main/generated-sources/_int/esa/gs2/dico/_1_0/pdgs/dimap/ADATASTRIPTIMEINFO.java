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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour A_DATASTRIP_TIME_INFO complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATASTRIP_TIME_INFO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATASTRIP_SENSING_START" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *         &lt;element name="DATASTRIP_SENSING_STOP" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATASTRIP_TIME_INFO", propOrder = {
    "datastripsensingstart",
    "datastripsensingstop"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFODS.DatastripTimeInfo.class
})
public class ADATASTRIPTIMEINFO {

    @XmlElement(name = "DATASTRIP_SENSING_START", required = true)
    protected XMLGregorianCalendar datastripsensingstart;
    @XmlElement(name = "DATASTRIP_SENSING_STOP", required = true)
    protected XMLGregorianCalendar datastripsensingstop;

    /**
     * Obtient la valeur de la propriété datastripsensingstart.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATASTRIPSENSINGSTART() {
        return datastripsensingstart;
    }

    /**
     * Définit la valeur de la propriété datastripsensingstart.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATASTRIPSENSINGSTART(XMLGregorianCalendar value) {
        this.datastripsensingstart = value;
    }

    /**
     * Obtient la valeur de la propriété datastripsensingstop.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATASTRIPSENSINGSTOP() {
        return datastripsensingstop;
    }

    /**
     * Définit la valeur de la propriété datastripsensingstop.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATASTRIPSENSINGSTOP(XMLGregorianCalendar value) {
        this.datastripsensingstop = value;
    }

}

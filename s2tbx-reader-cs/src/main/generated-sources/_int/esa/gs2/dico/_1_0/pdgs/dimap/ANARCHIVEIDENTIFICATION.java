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
import _int.esa.gs2.dico._1_0.pdgs.center.AS2ARCHIVINGCENTRE;


/**
 * <p>Classe Java pour AN_ARCHIVE_IDENTIFICATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ARCHIVE_IDENTIFICATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ARCHIVING_CENTRE" type="{http://gs2.esa.int/DICO/1.0/PDGS/center/}A_S2_ARCHIVING_CENTRE"/>
 *         &lt;element name="ARCHIVING_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_ARCHIVE_IDENTIFICATION", propOrder = {
    "archivingcentre",
    "archivingtime"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFOL0L1AL1B.ArchivingInfo.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFOL1C.ArchivingInfo.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFODS.ArchivingInfo.class
})
public class ANARCHIVEIDENTIFICATION {

    @XmlElement(name = "ARCHIVING_CENTRE", required = true)
    protected AS2ARCHIVINGCENTRE archivingcentre;
    @XmlElement(name = "ARCHIVING_TIME", required = true)
    protected XMLGregorianCalendar archivingtime;

    /**
     * Obtient la valeur de la propriété archivingcentre.
     * 
     * @return
     *     possible object is
     *     {@link AS2ARCHIVINGCENTRE }
     *     
     */
    public AS2ARCHIVINGCENTRE getARCHIVINGCENTRE() {
        return archivingcentre;
    }

    /**
     * Définit la valeur de la propriété archivingcentre.
     * 
     * @param value
     *     allowed object is
     *     {@link AS2ARCHIVINGCENTRE }
     *     
     */
    public void setARCHIVINGCENTRE(AS2ARCHIVINGCENTRE value) {
        this.archivingcentre = value;
    }

    /**
     * Obtient la valeur de la propriété archivingtime.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getARCHIVINGTIME() {
        return archivingtime;
    }

    /**
     * Définit la valeur de la propriété archivingtime.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setARCHIVINGTIME(XMLGregorianCalendar value) {
        this.archivingtime = value;
    }

}

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
 * Quality assessement created by INIT_LOC_INV : Planimetric stabililty performance for the datastrip
 * 
 * <p>Classe Java pour A_PLANIMETRIC_STABILITY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PLANIMETRIC_STABILITY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VALUE" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="MEASUREMENT_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_PLANIMETRIC_STABILITY", propOrder = {
    "value",
    "measurementdate"
})
public class APLANIMETRICSTABILITY {

    @XmlElement(name = "VALUE")
    protected double value;
    @XmlElement(name = "MEASUREMENT_DATE", required = true)
    protected XMLGregorianCalendar measurementdate;

    /**
     * Obtient la valeur de la propriété value.
     * 
     */
    public double getVALUE() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     * 
     */
    public void setVALUE(double value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété measurementdate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMEASUREMENTDATE() {
        return measurementdate;
    }

    /**
     * Définit la valeur de la propriété measurementdate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMEASUREMENTDATE(XMLGregorianCalendar value) {
        this.measurementdate = value;
    }

}

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
 * Computed by anaTM
 * 
 * <p>Classe Java pour A_DATATION_QUALITY_ASSESSMENT complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATATION_QUALITY_ASSESSMENT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Data_Content_Quality" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATA_CONTENT_QUALITY"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DATATION_QUALITY_ASSESSMENT", propOrder = {
    "dataContentQuality"
})
public class ADATATIONQUALITYASSESSMENT {

    @XmlElement(name = "Data_Content_Quality", required = true)
    protected ADATACONTENTQUALITY dataContentQuality;

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

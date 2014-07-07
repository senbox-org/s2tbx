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
 * <p>Classe Java pour A_RADIOMETRIC_INFO_USERL1C complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_RADIOMETRIC_INFO_USERL1C">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Reflectance_quantification_value" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RESAMPLE_DATA"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_RADIOMETRIC_INFO_USERL1C", propOrder = {
    "reflectanceQuantificationValue"
})
public class ARADIOMETRICINFOUSERL1C {

    @XmlElement(name = "Reflectance_quantification_value", required = true)
    protected ARESAMPLEDATA reflectanceQuantificationValue;

    /**
     * Obtient la valeur de la propriété reflectanceQuantificationValue.
     * 
     * @return
     *     possible object is
     *     {@link ARESAMPLEDATA }
     *     
     */
    public ARESAMPLEDATA getReflectanceQuantificationValue() {
        return reflectanceQuantificationValue;
    }

    /**
     * Définit la valeur de la propriété reflectanceQuantificationValue.
     * 
     * @param value
     *     allowed object is
     *     {@link ARESAMPLEDATA }
     *     
     */
    public void setReflectanceQuantificationValue(ARESAMPLEDATA value) {
        this.reflectanceQuantificationValue = value;
    }

}

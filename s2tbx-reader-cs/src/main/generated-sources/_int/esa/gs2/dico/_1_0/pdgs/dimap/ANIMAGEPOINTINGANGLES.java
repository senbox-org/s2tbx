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
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHDEGUNITATTR;


/**
 * Pointing angles 
 * 
 * <p>Classe Java pour AN_IMAGE_POINTING_ANGLES complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_IMAGE_POINTING_ANGLES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PSI_X" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *         &lt;element name="PSI_Y" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_IMAGE_POINTING_ANGLES", propOrder = {
    "psix",
    "psiy"
})
public class ANIMAGEPOINTINGANGLES {

    @XmlElement(name = "PSI_X", required = true)
    protected ADOUBLEWITHDEGUNITATTR psix;
    @XmlElement(name = "PSI_Y", required = true)
    protected ADOUBLEWITHDEGUNITATTR psiy;

    /**
     * Obtient la valeur de la propriété psix.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public ADOUBLEWITHDEGUNITATTR getPSIX() {
        return psix;
    }

    /**
     * Définit la valeur de la propriété psix.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public void setPSIX(ADOUBLEWITHDEGUNITATTR value) {
        this.psix = value;
    }

    /**
     * Obtient la valeur de la propriété psiy.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public ADOUBLEWITHDEGUNITATTR getPSIY() {
        return psiy;
    }

    /**
     * Définit la valeur de la propriété psiy.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public void setPSIY(ADOUBLEWITHDEGUNITATTR value) {
        this.psiy = value;
    }

}

//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.misc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Rotation, translation and homothety transformation uncertainties (X,Y,Z uncertainties)
 * 
 * <p>Classe Java pour A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ROTATION" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}AN_UNCERTAINTIES_XYZ_TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSLATION" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}AN_UNCERTAINTIES_XYZ_TYPE" minOccurs="0"/>
 *         &lt;element name="HOMOTHETY" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}AN_UNCERTAINTIES_XYZ_TYPE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE", propOrder = {
    "rotation",
    "translation",
    "homothety"
})
public class AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPE {

    @XmlElement(name = "ROTATION")
    protected ANUNCERTAINTIESXYZTYPE rotation;
    @XmlElement(name = "TRANSLATION")
    protected ANUNCERTAINTIESXYZTYPE translation;
    @XmlElement(name = "HOMOTHETY")
    protected ANUNCERTAINTIESXYZTYPE homothety;

    /**
     * Obtient la valeur de la propriété rotation.
     * 
     * @return
     *     possible object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public ANUNCERTAINTIESXYZTYPE getROTATION() {
        return rotation;
    }

    /**
     * Définit la valeur de la propriété rotation.
     * 
     * @param value
     *     allowed object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public void setROTATION(ANUNCERTAINTIESXYZTYPE value) {
        this.rotation = value;
    }

    /**
     * Obtient la valeur de la propriété translation.
     * 
     * @return
     *     possible object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public ANUNCERTAINTIESXYZTYPE getTRANSLATION() {
        return translation;
    }

    /**
     * Définit la valeur de la propriété translation.
     * 
     * @param value
     *     allowed object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public void setTRANSLATION(ANUNCERTAINTIESXYZTYPE value) {
        this.translation = value;
    }

    /**
     * Obtient la valeur de la propriété homothety.
     * 
     * @return
     *     possible object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public ANUNCERTAINTIESXYZTYPE getHOMOTHETY() {
        return homothety;
    }

    /**
     * Définit la valeur de la propriété homothety.
     * 
     * @param value
     *     allowed object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public void setHOMOTHETY(ANUNCERTAINTIESXYZTYPE value) {
        this.homothety = value;
    }

}

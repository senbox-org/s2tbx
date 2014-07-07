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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Rotation, translation and homothety transformation uncertainties (X,Y,Z uncertainties) in lower case
 * 
 * <p>Classe Java pour A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE_LOWER_CASE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE_LOWER_CASE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Rotation" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}AN_UNCERTAINTIES_XYZ_TYPE" minOccurs="0"/>
 *         &lt;element name="Translation" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}AN_UNCERTAINTIES_XYZ_TYPE" minOccurs="0"/>
 *         &lt;element name="Homothety" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}AN_UNCERTAINTIES_XYZ_TYPE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_ROTATION_TRANSLATION_HOMOTHETY_UNCERTAINTIES_TYPE_LOWER_CASE", propOrder = {
    "rotation",
    "translation",
    "homothety"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AREFINEDCORRECTIONS.FocalPlaneState.class
})
public class AROTATIONTRANSLATIONHOMOTHETYUNCERTAINTIESTYPELOWERCASE {

    @XmlElement(name = "Rotation")
    protected ANUNCERTAINTIESXYZTYPE rotation;
    @XmlElement(name = "Translation")
    protected ANUNCERTAINTIESXYZTYPE translation;
    @XmlElement(name = "Homothety")
    protected ANUNCERTAINTIESXYZTYPE homothety;

    /**
     * Obtient la valeur de la propriété rotation.
     * 
     * @return
     *     possible object is
     *     {@link ANUNCERTAINTIESXYZTYPE }
     *     
     */
    public ANUNCERTAINTIESXYZTYPE getRotation() {
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
    public void setRotation(ANUNCERTAINTIESXYZTYPE value) {
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
    public ANUNCERTAINTIESXYZTYPE getTranslation() {
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
    public void setTranslation(ANUNCERTAINTIESXYZTYPE value) {
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
    public ANUNCERTAINTIESXYZTYPE getHomothety() {
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
    public void setHomothety(ANUNCERTAINTIESXYZTYPE value) {
        this.homothety = value;
    }

}

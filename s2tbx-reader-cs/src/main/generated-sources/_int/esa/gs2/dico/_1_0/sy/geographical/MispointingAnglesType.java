//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.geographical;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import _int.esa.gs2.dico._1_0.sy.misc.AngleType;


/**
 * <p>Classe Java pour Mispointing_Angles_Type complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Mispointing_Angles_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pitch" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}Angle_Type"/>
 *         &lt;element name="Roll" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}Angle_Type"/>
 *         &lt;element name="Yaw" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}Angle_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Mispointing_Angles_Type", propOrder = {
    "pitch",
    "roll",
    "yaw"
})
public class MispointingAnglesType {

    @XmlElement(name = "Pitch", required = true)
    protected AngleType pitch;
    @XmlElement(name = "Roll", required = true)
    protected AngleType roll;
    @XmlElement(name = "Yaw", required = true)
    protected AngleType yaw;

    /**
     * Obtient la valeur de la propriété pitch.
     * 
     * @return
     *     possible object is
     *     {@link AngleType }
     *     
     */
    public AngleType getPitch() {
        return pitch;
    }

    /**
     * Définit la valeur de la propriété pitch.
     * 
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *     
     */
    public void setPitch(AngleType value) {
        this.pitch = value;
    }

    /**
     * Obtient la valeur de la propriété roll.
     * 
     * @return
     *     possible object is
     *     {@link AngleType }
     *     
     */
    public AngleType getRoll() {
        return roll;
    }

    /**
     * Définit la valeur de la propriété roll.
     * 
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *     
     */
    public void setRoll(AngleType value) {
        this.roll = value;
    }

    /**
     * Obtient la valeur de la propriété yaw.
     * 
     * @return
     *     possible object is
     *     {@link AngleType }
     *     
     */
    public AngleType getYaw() {
        return yaw;
    }

    /**
     * Définit la valeur de la propriété yaw.
     * 
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *     
     */
    public void setYaw(AngleType value) {
        this.yaw = value;
    }

}

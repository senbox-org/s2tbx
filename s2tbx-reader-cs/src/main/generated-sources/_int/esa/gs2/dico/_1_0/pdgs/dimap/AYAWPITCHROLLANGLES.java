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
 * Yaw, pitch, and roll angles
 * 
 * <p>Classe Java pour A_YAW_PITCH_ROLL_ANGLES complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_YAW_PITCH_ROLL_ANGLES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ROLL" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *         &lt;element name="PITCH" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *         &lt;element name="YAW" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_YAW_PITCH_ROLL_ANGLES", propOrder = {
    "roll",
    "pitch",
    "yaw"
})
public class AYAWPITCHROLLANGLES {

    @XmlElement(name = "ROLL", required = true)
    protected ADOUBLEWITHDEGUNITATTR roll;
    @XmlElement(name = "PITCH", required = true)
    protected ADOUBLEWITHDEGUNITATTR pitch;
    @XmlElement(name = "YAW", required = true)
    protected ADOUBLEWITHDEGUNITATTR yaw;

    /**
     * Obtient la valeur de la propriété roll.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public ADOUBLEWITHDEGUNITATTR getROLL() {
        return roll;
    }

    /**
     * Définit la valeur de la propriété roll.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public void setROLL(ADOUBLEWITHDEGUNITATTR value) {
        this.roll = value;
    }

    /**
     * Obtient la valeur de la propriété pitch.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public ADOUBLEWITHDEGUNITATTR getPITCH() {
        return pitch;
    }

    /**
     * Définit la valeur de la propriété pitch.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public void setPITCH(ADOUBLEWITHDEGUNITATTR value) {
        this.pitch = value;
    }

    /**
     * Obtient la valeur de la propriété yaw.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public ADOUBLEWITHDEGUNITATTR getYAW() {
        return yaw;
    }

    /**
     * Définit la valeur de la propriété yaw.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public void setYAW(ADOUBLEWITHDEGUNITATTR value) {
        this.yaw = value;
    }

}

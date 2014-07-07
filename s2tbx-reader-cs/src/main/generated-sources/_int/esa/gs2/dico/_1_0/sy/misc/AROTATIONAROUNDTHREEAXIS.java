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
 * <p>Classe Java pour A_ROTATION_AROUND_THREE_AXIS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_ROTATION_AROUND_THREE_AXIS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="R1" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_AN_AXIS"/>
 *         &lt;element name="R2" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_AN_AXIS"/>
 *         &lt;element name="R3" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_AN_AXIS"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_ROTATION_AROUND_THREE_AXIS", propOrder = {
    "r1",
    "r2",
    "r3"
})
public class AROTATIONAROUNDTHREEAXIS {

    @XmlElement(name = "R1", required = true)
    protected AROTATIONAROUNDANAXIS r1;
    @XmlElement(name = "R2", required = true)
    protected AROTATIONAROUNDANAXIS r2;
    @XmlElement(name = "R3", required = true)
    protected AROTATIONAROUNDANAXIS r3;

    /**
     * Obtient la valeur de la propriété r1.
     * 
     * @return
     *     possible object is
     *     {@link AROTATIONAROUNDANAXIS }
     *     
     */
    public AROTATIONAROUNDANAXIS getR1() {
        return r1;
    }

    /**
     * Définit la valeur de la propriété r1.
     * 
     * @param value
     *     allowed object is
     *     {@link AROTATIONAROUNDANAXIS }
     *     
     */
    public void setR1(AROTATIONAROUNDANAXIS value) {
        this.r1 = value;
    }

    /**
     * Obtient la valeur de la propriété r2.
     * 
     * @return
     *     possible object is
     *     {@link AROTATIONAROUNDANAXIS }
     *     
     */
    public AROTATIONAROUNDANAXIS getR2() {
        return r2;
    }

    /**
     * Définit la valeur de la propriété r2.
     * 
     * @param value
     *     allowed object is
     *     {@link AROTATIONAROUNDANAXIS }
     *     
     */
    public void setR2(AROTATIONAROUNDANAXIS value) {
        this.r2 = value;
    }

    /**
     * Obtient la valeur de la propriété r3.
     * 
     * @return
     *     possible object is
     *     {@link AROTATIONAROUNDANAXIS }
     *     
     */
    public AROTATIONAROUNDANAXIS getR3() {
        return r3;
    }

    /**
     * Définit la valeur de la propriété r3.
     * 
     * @param value
     *     allowed object is
     *     {@link AROTATIONAROUNDANAXIS }
     *     
     */
    public void setR3(AROTATIONAROUNDANAXIS value) {
        this.r3 = value;
    }

}

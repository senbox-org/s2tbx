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
 * <p>Classe Java pour A_ROTATION_AROUND_THREE_AXIS_AND_SCALE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_ROTATION_AROUND_THREE_AXIS_AND_SCALE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="R1" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_AN_AXIS"/>
 *         &lt;element name="R2" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_AN_AXIS"/>
 *         &lt;element name="R3" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_ROTATION_AROUND_AN_AXIS"/>
 *         &lt;element name="SCALE_FACTOR" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="COMBINATION_ORDER">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="SCALE_THEN_ROTATION"/>
 *               &lt;enumeration value="ROTATION_THEN_SCALE"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_ROTATION_AROUND_THREE_AXIS_AND_SCALE", propOrder = {
    "r1",
    "r2",
    "r3",
    "scalefactor",
    "combinationorder"
})
public class AROTATIONAROUNDTHREEAXISANDSCALE {

    @XmlElement(name = "R1", required = true)
    protected AROTATIONAROUNDANAXIS r1;
    @XmlElement(name = "R2", required = true)
    protected AROTATIONAROUNDANAXIS r2;
    @XmlElement(name = "R3", required = true)
    protected AROTATIONAROUNDANAXIS r3;
    @XmlElement(name = "SCALE_FACTOR")
    protected double scalefactor;
    @XmlElement(name = "COMBINATION_ORDER", required = true)
    protected String combinationorder;

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

    /**
     * Obtient la valeur de la propriété scalefactor.
     * 
     */
    public double getSCALEFACTOR() {
        return scalefactor;
    }

    /**
     * Définit la valeur de la propriété scalefactor.
     * 
     */
    public void setSCALEFACTOR(double value) {
        this.scalefactor = value;
    }

    /**
     * Obtient la valeur de la propriété combinationorder.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMBINATIONORDER() {
        return combinationorder;
    }

    /**
     * Définit la valeur de la propriété combinationorder.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMBINATIONORDER(String value) {
        this.combinationorder = value;
    }

}

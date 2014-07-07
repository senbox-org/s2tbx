//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.misc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour A_ROTATION_AROUND_AN_AXIS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_ROTATION_AROUND_AN_AXIS">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *       &lt;attribute name="axis" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_MRAD_UNIT" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_ROTATION_AROUND_AN_AXIS", propOrder = {
    "value"
})
public class AROTATIONAROUNDANAXIS {

    @XmlValue
    protected double value;
    @XmlAttribute(name = "axis", required = true)
    protected String axis;
    @XmlAttribute(name = "unit", required = true)
    protected AMRADUNIT unit;

    /**
     * Obtient la valeur de la propriété value.
     * 
     */
    public double getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     * 
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété axis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAxis() {
        return axis;
    }

    /**
     * Définit la valeur de la propriété axis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAxis(String value) {
        this.axis = value;
    }

    /**
     * Obtient la valeur de la propriété unit.
     * 
     * @return
     *     possible object is
     *     {@link AMRADUNIT }
     *     
     */
    public AMRADUNIT getUnit() {
        return unit;
    }

    /**
     * Définit la valeur de la propriété unit.
     * 
     * @param value
     *     allowed object is
     *     {@link AMRADUNIT }
     *     
     */
    public void setUnit(AMRADUNIT value) {
        this.unit = value;
    }

}

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
 * double value expressed in seconds ('ns' unit attribute)
 * 
 * <p>Classe Java pour A_DOUBLE_WITH_NS_UNIT_ATTR complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DOUBLE_WITH_NS_UNIT_ATTR">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_NS_UNIT" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DOUBLE_WITH_NS_UNIT_ATTR", propOrder = {
    "value"
})
public class ADOUBLEWITHNSUNITATTR {

    @XmlValue
    protected double value;
    @XmlAttribute(name = "unit", required = true)
    protected ANSUNIT unit;

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
     * Obtient la valeur de la propriété unit.
     * 
     * @return
     *     possible object is
     *     {@link ANSUNIT }
     *     
     */
    public ANSUNIT getUnit() {
        return unit;
    }

    /**
     * Définit la valeur de la propriété unit.
     * 
     * @param value
     *     allowed object is
     *     {@link ANSUNIT }
     *     
     */
    public void setUnit(ANSUNIT value) {
        this.unit = value;
    }

}

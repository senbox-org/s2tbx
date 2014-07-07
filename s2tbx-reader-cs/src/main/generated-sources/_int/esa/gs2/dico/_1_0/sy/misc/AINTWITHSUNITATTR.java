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
 * int value expressed in seconds ('s' unit attribute)
 * 
 * <p>Classe Java pour A_INT_WITH_S_UNIT_ATTR complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_INT_WITH_S_UNIT_ATTR">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
 *       &lt;attribute name="unit" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_S_UNIT" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_INT_WITH_S_UNIT_ATTR", propOrder = {
    "value"
})
public class AINTWITHSUNITATTR {

    @XmlValue
    protected int value;
    @XmlAttribute(name = "unit", required = true)
    protected ASUNIT unit;

    /**
     * Obtient la valeur de la propriété value.
     * 
     */
    public int getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     * 
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété unit.
     * 
     * @return
     *     possible object is
     *     {@link ASUNIT }
     *     
     */
    public ASUNIT getUnit() {
        return unit;
    }

    /**
     * Définit la valeur de la propriété unit.
     * 
     * @param value
     *     allowed object is
     *     {@link ASUNIT }
     *     
     */
    public void setUnit(ASUNIT value) {
        this.unit = value;
    }

}

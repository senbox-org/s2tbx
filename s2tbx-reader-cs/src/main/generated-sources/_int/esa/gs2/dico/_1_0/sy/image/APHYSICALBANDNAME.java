//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.image;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_PHYSICAL_BAND_NAME.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_PHYSICAL_BAND_NAME">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="B1"/>
 *     &lt;enumeration value="B2"/>
 *     &lt;enumeration value="B3"/>
 *     &lt;enumeration value="B4"/>
 *     &lt;enumeration value="B5"/>
 *     &lt;enumeration value="B6"/>
 *     &lt;enumeration value="B7"/>
 *     &lt;enumeration value="B8"/>
 *     &lt;enumeration value="B8A"/>
 *     &lt;enumeration value="B9"/>
 *     &lt;enumeration value="B10"/>
 *     &lt;enumeration value="B11"/>
 *     &lt;enumeration value="B12"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_PHYSICAL_BAND_NAME")
@XmlEnum
public enum APHYSICALBANDNAME {

    @XmlEnumValue("B1")
    B_1("B1"),
    @XmlEnumValue("B2")
    B_2("B2"),
    @XmlEnumValue("B3")
    B_3("B3"),
    @XmlEnumValue("B4")
    B_4("B4"),
    @XmlEnumValue("B5")
    B_5("B5"),
    @XmlEnumValue("B6")
    B_6("B6"),
    @XmlEnumValue("B7")
    B_7("B7"),
    @XmlEnumValue("B8")
    B_8("B8"),
    @XmlEnumValue("B8A")
    B_8_A("B8A"),
    @XmlEnumValue("B9")
    B_9("B9"),
    @XmlEnumValue("B10")
    B_10("B10"),
    @XmlEnumValue("B11")
    B_11("B11"),
    @XmlEnumValue("B12")
    B_12("B12");
    private final String value;

    APHYSICALBANDNAME(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static APHYSICALBANDNAME fromValue(String v) {
        for (APHYSICALBANDNAME c: APHYSICALBANDNAME.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

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
 * <p>Classe Java pour A_PRODUCT_TYPE.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_PRODUCT_TYPE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="S2MSI0"/>
 *     &lt;enumeration value="S2MSI1A"/>
 *     &lt;enumeration value="S2MSI1B"/>
 *     &lt;enumeration value="S2MSI1C"/>
 *     &lt;enumeration value="S2MSI2Ap"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_PRODUCT_TYPE")
@XmlEnum
public enum APRODUCTTYPE {

    @XmlEnumValue("S2MSI0")
    S_2_MSI_0("S2MSI0"),
    @XmlEnumValue("S2MSI1A")
    S_2_MSI_1_A("S2MSI1A"),
    @XmlEnumValue("S2MSI1B")
    S_2_MSI_1_B("S2MSI1B"),
    @XmlEnumValue("S2MSI1C")
    S_2_MSI_1_C("S2MSI1C"),
    @XmlEnumValue("S2MSI2Ap")
    S_2_MSI_2_AP("S2MSI2Ap");
    private final String value;

    APRODUCTTYPE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static APRODUCTTYPE fromValue(String v) {
        for (APRODUCTTYPE c: APRODUCTTYPE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

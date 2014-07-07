//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.orbital;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour AN_ORBIT_TYPE.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="AN_ORBIT_TYPE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="predicted"/>
 *     &lt;enumeration value="restituted"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AN_ORBIT_TYPE")
@XmlEnum
public enum ANORBITTYPE {

    @XmlEnumValue("predicted")
    PREDICTED("predicted"),
    @XmlEnumValue("restituted")
    RESTITUTED("restituted");
    private final String value;

    ANORBITTYPE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ANORBITTYPE fromValue(String v) {
        for (ANORBITTYPE c: ANORBITTYPE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

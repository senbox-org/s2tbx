//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_SWATH_POSITION.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_SWATH_POSITION">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="begin"/>
 *     &lt;enumeration value="center"/>
 *     &lt;enumeration value="end"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_SWATH_POSITION")
@XmlEnum
public enum ASWATHPOSITION {

    @XmlEnumValue("begin")
    BEGIN("begin"),
    @XmlEnumValue("center")
    CENTER("center"),
    @XmlEnumValue("end")
    END("end");
    private final String value;

    ASWATHPOSITION(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ASWATHPOSITION fromValue(String v) {
        for (ASWATHPOSITION c: ASWATHPOSITION.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

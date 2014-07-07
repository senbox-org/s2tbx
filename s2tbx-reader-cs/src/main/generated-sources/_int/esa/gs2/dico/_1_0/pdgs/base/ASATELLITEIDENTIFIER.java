//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.base;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_SATELLITE_IDENTIFIER.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_SATELLITE_IDENTIFIER">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="S2A"/>
 *     &lt;enumeration value="S2B"/>
 *     &lt;enumeration value="S2_"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_SATELLITE_IDENTIFIER")
@XmlEnum
public enum ASATELLITEIDENTIFIER {

    @XmlEnumValue("S2A")
    S_2_A("S2A"),
    @XmlEnumValue("S2B")
    S_2_B("S2B"),
    @XmlEnumValue("S2_")
    S_2("S2_");
    private final String value;

    ASATELLITEIDENTIFIER(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ASATELLITEIDENTIFIER fromValue(String v) {
        for (ASATELLITEIDENTIFIER c: ASATELLITEIDENTIFIER.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

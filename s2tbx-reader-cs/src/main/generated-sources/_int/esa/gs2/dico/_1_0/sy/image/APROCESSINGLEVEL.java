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
 * <p>Classe Java pour A_PROCESSING_LEVEL.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_PROCESSING_LEVEL">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Level-0"/>
 *     &lt;enumeration value="Level-1A"/>
 *     &lt;enumeration value="Level-1B"/>
 *     &lt;enumeration value="Level-1C"/>
 *     &lt;enumeration value="LeveI-2Ap"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_PROCESSING_LEVEL")
@XmlEnum
public enum APROCESSINGLEVEL {

    @XmlEnumValue("Level-0")
    LEVEL_0("Level-0"),
    @XmlEnumValue("Level-1A")
    LEVEL_1_A("Level-1A"),
    @XmlEnumValue("Level-1B")
    LEVEL_1_B("Level-1B"),
    @XmlEnumValue("Level-1C")
    LEVEL_1_C("Level-1C"),
    @XmlEnumValue("LeveI-2Ap")
    LEVE_I_2_AP("LeveI-2Ap");
    private final String value;

    APROCESSINGLEVEL(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static APROCESSINGLEVEL fromValue(String v) {
        for (APROCESSINGLEVEL c: APROCESSINGLEVEL.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

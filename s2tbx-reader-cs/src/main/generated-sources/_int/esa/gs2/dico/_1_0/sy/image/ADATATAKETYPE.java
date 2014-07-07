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
 * <p>Classe Java pour A_DATATAKE_TYPE.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_DATATAKE_TYPE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INS-NOBS"/>
 *     &lt;enumeration value="INS-EOBS"/>
 *     &lt;enumeration value="INS-DASC"/>
 *     &lt;enumeration value="INS-ABSR"/>
 *     &lt;enumeration value="INS-VIC"/>
 *     &lt;enumeration value="INS-RAW"/>
 *     &lt;enumeration value="INS-TST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_DATATAKE_TYPE")
@XmlEnum
public enum ADATATAKETYPE {

    @XmlEnumValue("INS-NOBS")
    INS_NOBS("INS-NOBS"),
    @XmlEnumValue("INS-EOBS")
    INS_EOBS("INS-EOBS"),
    @XmlEnumValue("INS-DASC")
    INS_DASC("INS-DASC"),
    @XmlEnumValue("INS-ABSR")
    INS_ABSR("INS-ABSR"),
    @XmlEnumValue("INS-VIC")
    INS_VIC("INS-VIC"),
    @XmlEnumValue("INS-RAW")
    INS_RAW("INS-RAW"),
    @XmlEnumValue("INS-TST")
    INS_TST("INS-TST");
    private final String value;

    ADATATAKETYPE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ADATATAKETYPE fromValue(String v) {
        for (ADATATAKETYPE c: ADATATAKETYPE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

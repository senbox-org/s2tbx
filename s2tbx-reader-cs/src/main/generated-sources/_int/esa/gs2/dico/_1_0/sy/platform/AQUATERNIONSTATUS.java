//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.platform;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_QUATERNION_STATUS.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_QUATERNION_STATUS">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOMINAL"/>
 *     &lt;enumeration value="OUT_OF_RANGE"/>
 *     &lt;enumeration value="LOST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_QUATERNION_STATUS", namespace = "http://gs2.esa.int/DICO/1.0/SY/platform/")
@XmlEnum
public enum AQUATERNIONSTATUS {

    NOMINAL,
    OUT_OF_RANGE,
    LOST;

    public String value() {
        return name();
    }

    public static AQUATERNIONSTATUS fromValue(String v) {
        return valueOf(v);
    }

}

//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.sy.misc;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_NSM.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_NSM">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PROPAGATED"/>
 *     &lt;enumeration value="FIRST_NAVIGATION_FIX"/>
 *     &lt;enumeration value="ESTIMATED_WITH_LEAST_SQUARE_METHOD"/>
 *     &lt;enumeration value="ESTIMATED_WITH_KALMAN_FILTER"/>
 *     &lt;enumeration value="LAST_KNOWN_GOOD_NS_PROPAGATED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_NSM")
@XmlEnum
public enum ANSM {

    PROPAGATED,
    FIRST_NAVIGATION_FIX,
    ESTIMATED_WITH_LEAST_SQUARE_METHOD,
    ESTIMATED_WITH_KALMAN_FILTER,
    LAST_KNOWN_GOOD_NS_PROPAGATED;

    public String value() {
        return name();
    }

    public static ANSM fromValue(String v) {
        return valueOf(v);
    }

}
